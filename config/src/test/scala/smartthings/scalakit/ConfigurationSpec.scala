package smartthings.scalakit

import java.io.File

import org.scalatest.{FunSpec, Matchers}
import pureconfig.generic.auto._
import scopt.OptionParser


class ConfigurationSpec extends FunSpec with Matchers {

  case class TestConfig(name: String)

  describe("Configuration") {
    it("should load default configuration") {
      val config = Configuration() { c =>
        pureconfig.loadConfig[TestConfig](c, "test").left.map(ConfigurationReadError)
      }

      config.isRight shouldBe true
      config.right.get shouldEqual TestConfig(name = "test")
    }

    it("should override default config with parsed values") {
      val config = Configuration(new OptionParser[TestConfig]("test") {
        opt[String]('n', "name").action { (n, c) => c.copy(name = n)}
        // This is strictly a noop so the pre parser external config values shows up in help output
        opt[File]('e', "external-config").optional().action { (_, c) => c}
      }, Array("--name=abc123")) { c =>
        pureconfig.loadConfig[TestConfig](c, "test").left.map(ConfigurationReadError)
      }

      config.isRight shouldBe true
      config.right.get shouldEqual TestConfig(name = "abc123")
    }

    it("should load external configuration") {
      val file = getClass.getResource("/external.conf").getPath
      val config = Configuration(Array(s"--external-config=$file")) { c =>
        pureconfig.loadConfig[TestConfig](c, "test").left.map(ConfigurationReadError)
      }

      config.isRight shouldBe true
      config.right.get shouldEqual TestConfig(name = "from-external")
    }
  }

}
