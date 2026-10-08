package zielu.gittoolbox.ui.config.v2.app

import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.properties.AtomicLazyProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import zielu.gittoolbox.ResBundle
import zielu.gittoolbox.config.AuthorNameType
import zielu.gittoolbox.config.DateType
import zielu.gittoolbox.config.MutableConfig
import zielu.gittoolbox.ui.config.v2.props.BoolProp
import zielu.gittoolbox.ui.config.v2.props.UiItems
import zielu.gittoolbox.ui.config.v2.props.ValueProp
import zielu.intellij.ui.GtFormUiEx
import javax.swing.JComponent
import javax.swing.ListCellRenderer

internal class BlamePage : GtFormUiEx<MutableConfig> {
  private val inlineAuthorNameType = AtomicLazyProperty {
    AuthorNameType.LASTNAME
  }
  private val inlineDateType = AtomicLazyProperty {
    DateType.AUTO
  }
  private val inlineShowSubject = AtomicBooleanProperty(true)
  private val inlineAlwaysShowBlameWhileDebugging = AtomicBooleanProperty(false)
  private val statusAuthorNameType = AtomicLazyProperty {
    AuthorNameType.LASTNAME
  }
  private lateinit var panel: DialogPanel
  private val uiItems = UiItems()

  override fun init() {
    val authorNameRenderer: ListCellRenderer<AuthorNameType?> = SimpleListCellRenderer.create("") {
      it?.getDisplayLabel()
    }
    val dateRenderer: ListCellRenderer<DateType?> = SimpleListCellRenderer.create("") {
      it?.getDisplayLabel()
    }
    panel = panel {
      group(ResBundle.message("configurable.app.editorInlineBlame.label")) {
        row(ResBundle.message("configurable.app.blameAuthorName")) {
          comboBox(CollectionComboBoxModel(AuthorNameType.allValues), authorNameRenderer)
            .bindItem(inlineAuthorNameType)
        }
        row(ResBundle.message("configurable.app.blameDateType")) {
          comboBox(CollectionComboBoxModel(DateType.allValues), dateRenderer)
            .bindItem(inlineDateType)
        }
        row {
          checkBox(ResBundle.message("configurable.app.blameSubject")).bindSelected(inlineShowSubject)
        }
        row {
          checkBox(ResBundle.message("configurable.app.alwaysShowInlineBlameWhileDebugging.label"))
            .bindSelected(inlineAlwaysShowBlameWhileDebugging)
        }
      }
      group(ResBundle.message("configurable.app.statusBlame.label")) {
        row(ResBundle.message("configurable.app.blameAuthorName")) {
          comboBox(CollectionComboBoxModel(AuthorNameType.allValues), authorNameRenderer)
            .bindItem(statusAuthorNameType)
        }
      }
    }
  }

  override fun fillFromState(state: MutableConfig) {
    uiItems.clear()

    uiItems.register(
      ValueProp(
        inlineAuthorNameType,
        state.app::blameInlineAuthorNameType
      ),
      ValueProp(
        inlineDateType,
        state.app::blameInlineDateType
      ),
      BoolProp(
        inlineShowSubject,
        state.app::blameInlineShowSubject
      ),
      BoolProp(
        inlineAlwaysShowBlameWhileDebugging,
        state.app::alwaysShowInlineBlameWhileDebugging
      ),
      ValueProp(
        statusAuthorNameType,
        state.app::blameStatusAuthorNameType
      )
    )
  }

  override fun isModified(): Boolean {
    return panel.isModified() || uiItems.isModified()
  }

  override val content: JComponent
    get() = panel

  override fun afterStateSet() {
    panel.reset()
  }

  override fun applyToState(state: MutableConfig) {
    panel.apply()
    uiItems.apply()
  }
}
