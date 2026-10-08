package zielu.gittoolbox.ui.config.v2

import com.intellij.ui.dsl.builder.Row

internal class RowController(private val row: Row) {
  fun setVisible(visible: Boolean) {
    row.visible(visible)
  }
}
