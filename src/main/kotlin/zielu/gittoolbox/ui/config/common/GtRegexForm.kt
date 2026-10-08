package zielu.gittoolbox.ui.config.common

import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import zielu.gittoolbox.ResBundle.message
import zielu.gittoolbox.ResIcons
import zielu.gittoolbox.formatter.RegExpFormatter
import zielu.gittoolbox.ui.config.GtPatternFormatterData
import zielu.gittoolbox.ui.util.RegExpTextField
import zielu.intellij.ui.GtFormUiEx
import javax.swing.JComponent
import javax.swing.JLabel

internal class GtRegexForm : GtFormUiEx<GtPatternFormatterData> {
  private val patternUpdateListeners = arrayListOf<UpdateListener>()
  private var inputValue = ""
  private var outputValue = ""
  private var updateEnabled = false
  private lateinit var regexTextField: RegExpTextField
  private lateinit var panel: DialogPanel
  private lateinit var patternStatus: JLabel
  private lateinit var outputStatus: JLabel

  override fun init() {
    regexTextField = RegExpTextField()
    Disposer.register(this, regexTextField)

    panel = panel {
      row(message("commit.dialog.completion.pattern.label")) {
        cell(regexTextField).align(Align.FILL).resizableColumn()
        patternStatus = label("").component
      }
      row(message("commit.dialog.completion.pattern.input.label")) {
        textField().bindText(this@GtRegexForm::inputValue).align(Align.FILL)
      }
      row(message("commit.dialog.completion.pattern.output.label")) {
        textField().bindText(this@GtRegexForm::outputValue).align(Align.FILL).resizableColumn()
        outputStatus = label("").component
      }
    }

    regexTextField.addTextListener { text, error ->
      updatePatternStatus(error)
      updateOutput()
      patternUpdateListeners.forEach { it.invoke(text) }
    }
  }

  private fun updatePatternStatus(error: String?) {
    patternStatus.icon = if (error != null) ResIcons.Error else ResIcons.Ok
    patternStatus.toolTipText = error
  }

  private fun updateOutput() {
    if (updateEnabled) {
      panel.apply()
      updateOutput(regexTextField.text, inputValue)
    }
  }

  private fun updateOutput(pattern: String, testInput: String) {
    val formatted = RegExpFormatter.create(pattern).format(testInput)
    outputValue = formatted.text
    panel.apply()
    updateOutputStatus(formatted.matches)
  }

  private fun updateOutputStatus(matched: Boolean) {
    outputStatus.icon = if (matched) ResIcons.Ok else ResIcons.Warning
    outputStatus.toolTipText = if (matched)
      message("commit.dialog.completion.pattern.output.matched.label")
    else
      message("commit.dialog.completion.pattern.output.not.matched.label")
  }

  fun addPatternUpdateListener(listener: (String) -> Unit) {
    patternUpdateListeners.add(listener)
  }

  override fun fillFromState(state: GtPatternFormatterData) {
    inputValue = state.testInput
    regexTextField.text = state.pattern
  }

  override fun isModified(): Boolean {
    TODO("not implemented")
  }

  override fun applyToState(state: GtPatternFormatterData) {
    TODO("not implemented")
  }

  override fun afterStateSet() {
    // TODO
    panel.reset()
  }

  override val content: JComponent
    get() = panel
}

private typealias UpdateListener = (String) -> Unit
