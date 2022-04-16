package example

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UrlUtilsSpec extends AnyFlatSpec with Matchers {
  "isUrlMatchingBase" should "return true" in {
    val baseUrl = "http://bluepitaya.xyz"
    val url = "https://www.bluepitaya.xyz/about/me?v=1"

    UrlUtils.isUrlMatchingBase(baseUrl)(url) shouldEqual true
  }
}
