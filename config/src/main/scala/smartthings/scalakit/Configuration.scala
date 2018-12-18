/*
 * Copyright 2018 SmartThings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smartthings.scalakit

import java.io.{ByteArrayOutputStream, File}

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.error.ConfigReaderFailures
import scopt.OptionParser

object Configuration {

  private case class PreConfig(external: Option[File] = None)

  def apply[C]()(loader: Config => Either[ConfigurationReadError, C]): Either[ConfigurationError, C] = {
    apply(Array.empty)(loader)
  }

  def apply[C](args: Array[String])(loader: Config => Either[ConfigurationReadError, C]): Either[ConfigurationError, C] = {
    for {
      config <- loader(resolvedConfig(args))
    } yield config
  }

  def apply[C](parser: OptionParser[C], args: Array[String])(loader: Config => Either[ConfigurationReadError, C]): Either[ConfigurationError, C] = {
    for {
      config <- loader(resolvedConfig(args))
      stacked <- parser.parse(args, config).toRight(ConfigurationParseError(parser.usage))
    } yield stacked
  }

  private def resolvedConfig(args: Array[String]): Config = {
    // load external config path if given otherwise load default config
    parsePreConfig(args)
      .flatMap(_.external).map(ConfigFactory.parseFile)
      .getOrElse(ConfigFactory.load())
  }

  private def parsePreConfig(args: Array[String]): Option[PreConfig] = {
    val outCapture = new ByteArrayOutputStream()
    val errCapture = new ByteArrayOutputStream()

    Console.withOut(outCapture) {
      Console.withErr(errCapture) {
        new OptionParser[PreConfig]("preconfig") {
          opt[File]('e', "external-config").optional().action { (e, c) =>
            c.copy(external = Some(e))
          }
        }.parse(args, PreConfig())
      }
    }
  }

}

sealed trait ConfigurationError
case class ConfigurationReadError(error: ConfigReaderFailures) extends ConfigurationError
case class ConfigurationParseError(help: String) extends ConfigurationError
