import java.io.StringWriter

import org.scalatest.featurespec.AnyFeatureSpec
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.StringTemplateResolver
import scala.jdk.CollectionConverters._

class ThymeleafTest extends AnyFeatureSpec {
  private val ctx = new Context()
  private val resolver = new StringTemplateResolver
  private val templateEngine = {
    val result = new TemplateEngine
    result.setTemplateResolver(resolver)
    result
  }

  Feature("Resolve fields of a case-class") {
    val buffer = new StringWriter()
    case class SampleCaseClass(fieldName: String)
    ctx.setVariable("sample_case_class", SampleCaseClass("FieldValue"))
    templateEngine.process("""<CaseClass th:text="${sample_case_class.fieldName}"></CaseClass>""".stripMargin, ctx, buffer)
    assert(buffer.toString == """<CaseClass>FieldValue</CaseClass>""")
  }

  Feature("Iterate through map entries") {
    val buffer = new StringWriter()
    ctx.setVariable("sample_map", Map("A" -> 1, "B" -> 2).asJava)
    templateEngine.process(
      """<Map>
        |<Entry th:each="entry: ${sample_map}" th:attr="key=${entry.key}" th:text="${entry.value}"></Entry>
        |</Map>""".stripMargin, ctx, buffer)
    assert(buffer.toString == """<Map>
                                |<Entry key="A">1</Entry><Entry key="B">2</Entry>
                                |</Map>""".stripMargin)
  }
}
