package smartthings.scalakit

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.error.ConfigReaderFailures
import scopt.OptionParser

object Configuration {

  private case class PreConfig(external: Option[File] = None)

  def apply[C](parser: OptionParser[C], args: Array[String])(loader: Config => Either[ConfigurationReadError, C]): Either[ConfigurationError, C] = {
    apply(parser, args)(loader)
  }

  def apply[C](baseConfig: Config, parser: OptionParser[C], args: Array[String])(loader: Config => Either[ConfigurationReadError, C]): Either[ConfigurationError, C] = {
    val preConfig = new OptionParser[PreConfig]("preconfig") {
      opt[File]('e', "external-config").action { (e, c) =>
        c.copy(external = Some(e))
      }
    }.parse(args, PreConfig())

    // load external config path if given otherwise load default config
    val resolvedConfig = preConfig
      .flatMap(_.external).map(ConfigFactory.parseFile)
      .getOrElse(ConfigFactory.load())

    for {
      config <- loader(resolvedConfig)
      stacked <- parser.parse(args, config).toRight(ConfigurationParseError(parser.usage))
    } yield stacked
  }

}

sealed trait ConfigurationError
case class ConfigurationReadError(error: ConfigReaderFailures) extends ConfigurationError
case class ConfigurationParseError(help: String) extends ConfigurationError
