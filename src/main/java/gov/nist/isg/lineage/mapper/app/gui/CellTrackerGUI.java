// NIST-developed software is provided by NIST as a public service. You may use, copy and distribute copies of the software in any medium, provided that you keep intact this entire notice. You may improve, modify and create derivative works of the software or any portion of the software, and you may copy and distribute such modifications or works. Modified works should carry a notice stating that you changed the software and should note the date and nature of any such change. Please explicitly acknowledge the National Institute of Standards and Technology as the source of the software.

// NIST-developed software is expressly provided "AS IS." NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED, IN FACT OR ARISING BY OPERATION OF LAW, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT AND DATA ACCURACY. NIST NEITHER REPRESENTS NOR WARRANTS THAT THE OPERATION OF THE SOFTWARE WILL BE UNINTERRUPTED OR ERROR-FREE, OR THAT ANY DEFECTS WILL BE CORRECTED. NIST DOES NOT WARRANT OR MAKE ANY REPRESENTATIONS REGARDING THE USE OF THE SOFTWARE OR THE RESULTS THEREOF, INCLUDING BUT NOT LIMITED TO THE CORRECTNESS, ACCURACY, RELIABILITY, OR USEFULNESS OF THE SOFTWARE.

// You are solely responsible for determining the appropriateness of using and distributing the software and you assume all risks associated with its use, including but not limited to the risks and costs of program errors, compliance with applicable laws, damage to or loss of data, programs or equipment, and the unavailability or interruption of operation. This software is not intended to be used in any situation where a failure could cause risk of injury or damage to property. The software developed by NIST employees is not subject to copyright protection within the United States.

package gov.nist.isg.lineage.mapper.app.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;

import gov.nist.isg.lineage.mapper.app.TrackingAppParams;
import gov.nist.isg.lineage.mapper.app.gui.panels.AdvancedPanel;
import gov.nist.isg.lineage.mapper.app.gui.panels.ControlPanel;
import gov.nist.isg.lineage.mapper.app.gui.panels.HelpPanel;
import gov.nist.isg.lineage.mapper.app.gui.panels.OptionsPanel;
import gov.nist.isg.lineage.mapper.app.images.AppImageHelper;
import gov.nist.isg.lineage.mapper.lib.Log;
import ij.IJ;

/**
 * GUI to enable the user to specify the LineageMapper parameters.
 */
public class CellTrackerGUI extends JFrame {

  private OptionsPanel optionsPanel;
  private AdvancedPanel advancedPanel;
  private ControlPanel controlPanel;
  private TrackingAppParams params;

  /**
   * GUI to enable the user to specify the LineageMapper parameters.
   * @param params the TrackingAppParams instance backing the GUI parameters
   * @param show whether to display the GUI or not. The GUI contains the parameter validation
   *             logic, so when running headless a non-showing GUI is created to validate the
   *             specified parameters.
   */
  public CellTrackerGUI(TrackingAppParams params, boolean show) {
    super(TrackingAppParams.getAppTitle());

    this.params = params;

    this.setExtendedState(JFrame.NORMAL);
    Dimension guiSize = new Dimension(550, 550);
    this.setPreferredSize(guiSize);
    this.setMinimumSize(guiSize);


    init();
    initToolTips();

    if (show) {
      Log.mandatory("Starting Lineage Mapper");
      // Show the gui
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          dispApp();
        }
      });
    }

  }

  private void init() {

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    optionsPanel = new OptionsPanel(params);
    JScrollPane sp1 = new JScrollPane(optionsPanel);
    sp1.getVerticalScrollBar().setUnitIncrement(8);
    sp1.setBorder(new EmptyBorder(0, 0, 0, 0));
    tabbedPane.add("Options", sp1);

    advancedPanel = new AdvancedPanel(params);
    JScrollPane sp2 = new JScrollPane(advancedPanel);
    sp2.getVerticalScrollBar().setUnitIncrement(8);
    sp2.setBorder(new EmptyBorder(0, 0, 0, 0));
    tabbedPane.add("Advanced", sp2);


    tabbedPane.add("Help", new HelpPanel());

    controlPanel = new ControlPanel(params);


    this.setLayout(new BorderLayout());
    this.add(tabbedPane, BorderLayout.CENTER);
    this.add(controlPanel, BorderLayout.SOUTH);

    params.setGuiPane(this);
    params.loadPreferences();

  }

  private void initToolTips() {
    ToolTipManager manager = ToolTipManager.sharedInstance();
    manager.setDismissDelay(10000);
    manager.setInitialDelay(500);
    manager.setReshowDelay(500);
    manager.setLightWeightPopupEnabled(true);
  }

  public void copyToTrackingAppParams() {

    String errors = this.optionsPanel.getErrorString() + this.advancedPanel.getErrorString();
    if (!errors.isEmpty()) {
      Log.setLogLevel(Log.LogType.MANDATORY);
      Log.error("Invalid Parameter(s):");
      Log.error(errors);
      IJ.error("Invalid Parameter Found: See Log for Details");

      throw new IllegalArgumentException("Invalid Parameters found");
    }

    // copy params from GUI to the params object
    this.optionsPanel.pullParamsFromGUI();

    // copy params from GUI to the params object
    this.advancedPanel.pullParamsFromGUI();
  }


  public OptionsPanel getOptionsPanel() {
    return optionsPanel;
  }

  public AdvancedPanel getAdvancedPanel() {
    return advancedPanel;
  }

  public ControlPanel getControlPanel() {
    return controlPanel;
  }


  private void dispApp() {
    try {
      setIconImage(AppImageHelper.loadImage("tracker_icon.png").getImage());
    } catch (FileNotFoundException ignored) {
    }

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
      }
    });

    setLocationRelativeTo(null);
    setVisible(true);
  }

  /**
   * Check all sub-elements for errors
   * @return true if any contained element has an error
   */
  public boolean hasError() {
    return this.getOptionsPanel().hasError() || this.getAdvancedPanel().hasError();
  }


}
