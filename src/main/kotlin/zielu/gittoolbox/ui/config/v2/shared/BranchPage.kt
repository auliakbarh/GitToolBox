package zielu.gittoolbox.ui.config.v2.shared

import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.properties.AtomicLazyProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.execution.ParametersListUtil
import zielu.gittoolbox.ResBundle
import zielu.gittoolbox.branch.OutdatedBranchCleanupParams
import zielu.gittoolbox.config.MutableConfig
import zielu.gittoolbox.ui.config.v2.props.BoolProp
import zielu.gittoolbox.ui.config.v2.props.UiItems
import zielu.gittoolbox.ui.config.v2.props.ValueProp
import zielu.intellij.ui.GtFormUiEx
import javax.swing.JComponent

internal class BranchPage : GtFormUiEx<MutableConfig> {
  private val outdatedAutoCleanupEnabled = AtomicBooleanProperty(false)
  private val outdatedAutoCleanupInterval = AtomicLazyProperty {
    OutdatedBranchCleanupParams.DEFAULT_INTERVAL_HOURS
  }
  private val outdatedCleanupExcludedBranches = AtomicLazyProperty {
    ""
  }
  private lateinit var panel: DialogPanel

  private val uiItems = UiItems()
  init {
    Disposer.register(this, uiItems)
  }

  override val content: JComponent
    get() = panel

  override fun init() {
    panel = panel {
      group(ResBundle.message("configurable.shared.branchCleanup.section.title")) {
        row {
          checkBox(ResBundle.message("configurable.shared.branchCleanup.autoCleanupEnabled.label"))
            .bindSelected(outdatedAutoCleanupEnabled::get, outdatedAutoCleanupEnabled::set)
          spinner(
            OutdatedBranchCleanupParams.INTERVAL_MIN_HOURS..OutdatedBranchCleanupParams.INTERVAL_MAX_HOURS
          ).bindIntValue(outdatedAutoCleanupInterval::get, outdatedAutoCleanupInterval::set)
          label(ResBundle.message("configurable.shared.branchCleanup.autoCleanupUnits.label"))
        }
        row(ResBundle.message("configurable.shared.branchCleanup.exclusions.label")) {
          expandableTextField(
            ParametersListUtil.COLON_LINE_PARSER,
            ParametersListUtil.COLON_LINE_JOINER
          ).bindText(outdatedCleanupExcludedBranches::get, outdatedCleanupExcludedBranches::set)
            .align(AlignX.FILL)
            .comment(ResBundle.message("configurable.shared.branchCleanup.exclusions.comment"), 140)
        }
      }
    }
  }

  override fun fillFromState(state: MutableConfig) {
    uiItems.clear()

    uiItems.register(
      BoolProp(
        outdatedAutoCleanupEnabled,
        state.app.outdatedBranchesAutoCleanup::autoCheckEnabled
      ),
      ValueProp(
        outdatedAutoCleanupInterval,
        state.app.outdatedBranchesAutoCleanup::autoCheckIntervalHours
      ),
      ValueProp(
        outdatedCleanupExcludedBranches,
        { state.app.outdatedBranchesExclusionGlobs.joinToString(separator = ";") },
        { state.app.outdatedBranchesExclusionGlobs = it.split(';') }
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
