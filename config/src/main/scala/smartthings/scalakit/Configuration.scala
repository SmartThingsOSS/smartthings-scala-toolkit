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
