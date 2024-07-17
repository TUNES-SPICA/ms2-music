package org.music

import org.music.entity.track.TrackSplittingRule

import java.awt.event.WindowEvent
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import scala.swing.*
import scala.swing.event.*

/**
 * 可视化主程序
 */
object MainGUI extends SimpleSwingApplication {

  // 创建一个FileChooser对象，初始化为当前目录
  private val midChooser = new FileChooser(new File("."))
  private val mmlChooser = new FileChooser(new File("."))

  // 创建一个FileNameExtensionFilter，只允许选择.mid或.MID文件
  midChooser.fileFilter = new FileNameExtensionFilter("MIDI Files(.mid)", "mid", "MID")

  // 设置文件选择器的标题
  midChooser.title = "选择 MIDI 文件"
  mmlChooser.title = "选择保存目录"

  // 配置chooser2为仅选择目录
  mmlChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

  // 创建一个Button对象，用于触发文件选择对话框
  private val button = new Button {
    // 设置按钮的文本内容
    text = "选择 MIDI 文件"
  }

  // 创建一个Label对象，用于显示所选文件的路径
  private val label = new Label {
    // 初始状态下的文本提示
    text = "请选择文件"
  }

  // 创建一个FlowPanel对象，用于布局Button和Label组件
  private val mainPanel = new FlowPanel {
    // 将Button和Label添加到面板中
    contents += button
    contents += label
  }

  // 定义top方法，返回一个MainFrame对象，这是Swing应用程序的主窗口
  def top: MainFrame = new MainFrame {
    // 设置主窗口的标题
    title = "ms2-music"
    // 设置主窗口的内容为mainPanel
    contents = mainPanel

    // 设置窗口图标
    override val iconImage: Image = ImageIO.read(getClass.getResource("/log.jpg"))
    peer.setIconImage(iconImage)

    // 开始监听Button的事件
    listenTo(button)

    // 定义事件反应，当Button被点击时执行代码块
    reactions += {
      case ButtonClicked(b) => {
        // 显示文件选择对话框
        val midBut = midChooser.showOpenDialog(mainPanel)
        // 如果用户选择了文件
        if (midBut == FileChooser.Result.Approve) {
          // 更新Label的文本为所选文件的路径
          label.text = midChooser.selectedFile.getPath

          // 获取所选文件的路径
          val midName = midChooser.selectedFile.getName
          val midPath = midChooser.selectedFile.toPath
          // 显示目录选择对话框
          val mmlBut = mmlChooser.showOpenDialog(mainPanel)
          if (mmlBut == FileChooser.Result.Approve) {
            // 获取所选目录的File对象
            val mmlPath = mmlChooser.selectedFile.getAbsolutePath
            try {
              ParserWorkflow.convertMIDToMML(midPath, Paths.get(mmlPath, midName.substring(0, midName.lastIndexOf('.'))), TrackSplittingRule(true, false))
              JOptionPane.showMessageDialog(null, "文件生成成功", "成功", JOptionPane.INFORMATION_MESSAGE)
            } catch case e: Exception => JOptionPane.showMessageDialog(null, s"文件生成时发生错误: ${e.getMessage}", "错误", JOptionPane.ERROR_MESSAGE)
          }
        }
      }
    }

    // 添加窗口关闭事件监听器，当用户尝试关闭窗口时执行代码
    peer.addWindowListener(new java.awt.event.WindowAdapter() {
      // 重写windowClosing方法，当窗口关闭事件触发时调用System.exit(0)退出程序
      override def windowClosing(e: WindowEvent): Unit = {
        System.exit(0)
      }
    })
  }
}
