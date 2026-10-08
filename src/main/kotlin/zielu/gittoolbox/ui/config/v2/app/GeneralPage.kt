package zielu.gittoolbox.ui.config.v2.app

import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.properties.AtomicLazyProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.ex.MultiLineLabel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import zielu.gittoolbox.ResBundle
import zielu.gittoolbox.config.AbsoluteDateTimeStyle
import zielu.gittoolbox.config.MutableConfig
import zielu.gittoolbox.extension.update.UpdateProjectAction
import zielu.gittoolbox.ui.StatusPresenter
import zielu.gittoolbox.ui.StatusPresenters
import zielu.gittoolbox.ui.config.AbsoluteDateTimeStyleRenderer
import zielu.gittoolbox.ui.config.v2.PresenterPreview
import zielu.gittoolbox.ui.config.v2.props.BoolProp
import zielu.gittoolbox.ui.config.v2.props.UiItems
import zielu.gittoolbox.ui.config.v2.props.ValueProp
import zielu.gittoolbox.ui.update.UpdateProjectActionService
import zielu.intellij.ui.GtFormUiEx
import java.util.Vector
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.ListCellRenderer

internal class GeneralPage(
  private val appPages: AppPages
) : GtFormUiEx<MutableConfig> {
  private lateinit var panel: DialogPanel
  private val presentationMode = AtomicLazyProperty<StatusPresenter>(StatusPresenters::defaultPresenter)
  private val showStatusWidget = AtomicBooleanProperty(true)
  private val showChangesInStatusBar = AtomicBooleanProperty(true)
  private val showBlameWidget = AtomicBooleanProperty(true)
  private val showEditorInlineBlame = AtomicBooleanProperty(true)
  private val showProjectViewDecoration = AtomicBooleanProperty(true)
  private val behindTracker = AtomicBooleanProperty(true)
  private val updateProjectAction = AtomicLazyProperty {
    UpdateProjectActionService.getInstance().getDefault()
  }
  private val absoluteDateTypeStyle = AtomicLazyProperty {
    AbsoluteDateTimeStyle.FROM_LOCALE
  }
  private val uiItems = UiItems()

  override val content: JComponent
    get() = panel

  override fun init() {
    val presentationStatusBarPreview = JBLabel()
    val presentationProjectViewPreview = JBLabel()
    val presentationBehindTrackerPreview = JBLabel()
    val updatePresentationPreviews: (StatusPresenter) -> Unit = {
      presentationStatusBarPreview.text = PresenterPreview.getStatusBarPreview(it)
      presentationProjectViewPreview.text = PresenterPreview.getProjectViewPreview(it)
      presentationBehindTrackerPreview.text = PresenterPreview.getBehindTrackerPreview(it)
    }

    val updateActionComment = MultiLineLabel(ResBundle.message("update.project.action.description"))
    panel = panel {
      row(ResBundle.message("configurable.app.presentation.label")) {
        val renderer: ListCellRenderer<StatusPresenter?> = SimpleListCellRenderer.create("") { it?.label }
        val combo = comboBox(
          DefaultComboBoxModel(StatusPresenters.allPresenters()),
          renderer
        ).bindItem(presentationMode).component
        combo.addActionListener {
          val presenter = combo.selectedItem as StatusPresenter
          appPages.statusPresenter = presenter
          updatePresentationPreviews(presenter)
        }
      }
      separator()
      row(ResBundle.message("configurable.app.presentation.statusbar.preview")) {
        cell(presentationStatusBarPreview)
      }
      row(ResBundle.message("configurable.app.presentation.projectView.preview")) {
        cell(presentationProjectViewPreview)
      }
      row(ResBundle.message("configurable.app.presentation.behindTracker.preview")) {
        cell(presentationBehindTrackerPreview)
      }
      row {
        checkBox(ResBundle.message("configurable.app.showStatusWidget.label")).bindSelected(showStatusWidget)
      }
      row {
        checkBox(ResBundle.message("configurable.app.trackChanges.label")).bindSelected(showChangesInStatusBar)
      }
      row {
        checkBox(ResBundle.message("configurable.app.showBlame.label")).bindSelected(showBlameWidget)
      }
      row {
        checkBox(ResBundle.message("configurable.app.showEditorInlineBlame.label"))
          .bindSelected(showEditorInlineBlame)
      }
      row {
        checkBox(ResBundle.message("configurable.app.showProjectViewDecoration.label"))
          .bindSelected(showProjectViewDecoration)
      }
      row {
        checkBox(ResBundle.message("configurable.app.behindTrackerEnabled.label")).bindSelected(behindTracker)
      }
      row(ResBundle.message("update.project.action.label")) {
        val values = UpdateProjectActionService.getInstance().getAll()
        val renderer: ListCellRenderer<UpdateProjectAction?> = SimpleListCellRenderer.create("") { it?.getName() }
        comboBox(
          DefaultComboBoxModel(Vector(values)),
          renderer
        ).bindItem(updateProjectAction)
      }
      row {
        cell(updateActionComment)
      }
      row(ResBundle.message("configurable.app.absoluteDateTimeStyle.label")) {
        comboBox(
          DefaultComboBoxModel(AbsoluteDateTimeStyle.values()),
          AbsoluteDateTimeStyleRenderer()
        ).bindItem(absoluteDateTypeStyle)
      }
    }
  }

  override fun fillFromState(state: MutableConfig) {
    uiItems.clear()

    uiItems.register(
      ValueProp(
        presentationMode,
        state.app::getPresenter,
        state.app::setPresenter
      ),
      BoolProp(
        showStatusWidget,
        state.app::showStatusWidget
      ),
      BoolProp(
        showChangesInStatusBar,
        state.app::showChangesInStatusBar
      ),
      BoolProp(
        showBlameWidget,
        state.app::showBlameWidget
      ),
      BoolProp(
        showEditorInlineBlame,
        state.app::showEditorInlineBlame
      ),
      BoolProp(
        showProjectViewDecoration,
        state.app::showProjectViewStatus
      ),
      BoolProp(
        behindTracker,
        state.app::behindTracker
      ),
      ValueProp(
        updateProjectAction,
        state.app::getUpdateProjectAction,
        state.app::setUpdateProjectAction
      ),
      ValueProp(
        absoluteDateTypeStyle,
        state.app::absoluteDateTimeStyle
      )
    )
  }

  override fun afterStateSet() {
    panel.reset()
  }

  override fun isModified(): Boolean {
    return panel.isModified() || uiItems.isModified()
  }

  override fun applyToState(state: MutableConfig) {
    panel.apply()
    uiItems.apply()
  }
}
