package com.lightBoard.controls;

import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 *
 */

/**
 * @author Moham
 */
public class AppWindow {

    private JFrame frame;
    private DrawingPanel drawingPanel;
    private MasterControls mControls;
    private JTextField txtSpeed;
    private JTextField txtSmoothness;
    private JTextField txtTailLength;
    private JTextField txtTailThickness;
    private JButton button;
    private JButton button_1;
    private JButton button_2;
    private JButton button_3;
    private JButton button_4;
    private JButton button_5;

    private interface ExecuteMe {
        void execute();
    }

    ;


    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    AppWindow window = new AppWindow();
//                    window.frame.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


    /**
     * Create the application.
     */
    public AppWindow() {
        mControls = MasterControls.INSTANCE;
        initViews();
        initViewListeners();
//        mControls.startDrawing(drawingPanel);
    }


    /**
     * Initialize the contents of the frame.
     */
    private void initViews() {
        frame = new JFrame();
        frame.setBounds(100, 100, 650, 729);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{630};
        gridBagLayout.rowHeights = new int[]{173, 0};
        gridBagLayout.columnWeights = new double[]{0.0};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0};
        frame.getContentPane().setLayout(gridBagLayout);

        drawingPanel = new DrawingPanel(mControls);
        GridBagConstraints gbc_drawingPanel = new GridBagConstraints();
        gbc_drawingPanel.insets = new Insets(0, 0, 5, 0);
        gbc_drawingPanel.anchor = GridBagConstraints.NORTH;
        gbc_drawingPanel.fill = GridBagConstraints.BOTH;
        gbc_drawingPanel.weighty = 0.3;
        gbc_drawingPanel.gridx = 0;
        gbc_drawingPanel.gridy = 0;
        frame.getContentPane().add(drawingPanel, gbc_drawingPanel);

        JPanel controlsPanel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.anchor = GridBagConstraints.CENTER;
        gbc_panel.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 1;
        frame.getContentPane().add(controlsPanel, gbc_panel);

        JPanel panel = new JPanel();
        controlsPanel.add(panel);

//		panel.setLayout(new FormLayout(new ColumnSpec[] {
//				FormSpecs.RELATED_GAP_COLSPEC,
//				ColumnSpec.decode("97px"),
//				FormSpecs.UNRELATED_GAP_COLSPEC,
//				ColumnSpec.decode("97px"),
//				FormSpecs.UNRELATED_GAP_COLSPEC,
//				ColumnSpec.decode("110px"),
//				FormSpecs.UNRELATED_GAP_COLSPEC,
//				ColumnSpec.decode("116px"),
//				FormSpecs.RELATED_GAP_COLSPEC},
//			new RowSpec[] {
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("25px"),
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("25px"),
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("25px"),
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("25px"),
//				FormSpecs.RELATED_GAP_ROWSPEC,
//				RowSpec.decode("25px"),
//				FormSpecs.RELATED_GAP_ROWSPEC}));

        button = new JButton("Infinity");
        panel.add(button, "2, 2, fill, fill");

        button_1 = new JButton("Horizental");
        panel.add(button_1, "2, 8, fill, fill");

        button_2 = new JButton("Vertical");
        panel.add(button_2, "2, 10, fill, fill");

        button_3 = new JButton("\\");
        panel.add(button_3, "2, 4, fill, fill");

        button_4 = new JButton("/");
        panel.add(button_4, "2, 6, fill, fill");

        button_5 = new JButton("Pattern Color");
        panel.add(button_5, "4, 2, fill, fill");

        JLabel label = new JLabel("speed");
        panel.add(label, "6, 2, fill, fill");

        JLabel label_1 = new JLabel("smothness");
        panel.add(label_1, "6, 4, left, fill");

        JLabel label_2 = new JLabel("tail length");
        panel.add(label_2, "6, 6, left, fill");

        JLabel label_3 = new JLabel("Tail Thickness");
        panel.add(label_3, "6, 8, fill, fill");

        txtSpeed = new JTextField();
        txtSpeed.setText(mControls.getRepeatDelay() + "");
        txtSpeed.setColumns(10);
        panel.add(txtSpeed, "8, 2, fill, fill");

        txtSmoothness = new JTextField();
        txtSmoothness.setText(mControls.getSmoothness() + "");
        txtSmoothness.setColumns(10);
        panel.add(txtSmoothness, "8, 4, fill, fill");

        txtTailLength = new JTextField();
        txtTailLength.setText(mControls.getMaxBufferSize() + "");
        txtTailLength.setColumns(10);
        panel.add(txtTailLength, "8, 6, fill, fill");

        txtTailThickness = new JTextField();
        txtTailThickness.setText(mControls.getBrushSize() + "");
        txtTailThickness.setColumns(10);
        panel.add(txtTailThickness, "8, 8, fill, fill");
    }

    /**
     *
     */
    private void initViewListeners() {
        ExecuteMe speedExec = () -> {
            if (txtSpeed.getText().length() > 0)
                mControls.setRepeatDelay(Integer.parseInt(txtSpeed.getText()));
        };
//			txtSpeed.seton

        ExecuteMe smoothnessExec = () -> {
            if (txtSmoothness.getText().length() > 0)
                mControls.setSmoothness(Double.parseDouble(txtSmoothness.getText()));
        };

        ExecuteMe tailLengthExec = () -> {
            if (txtTailLength.getText().length() > 0)
                mControls.setMaxBufferSize(Integer.parseInt(txtTailLength.getText()));
        };

        ExecuteMe tailWidthExec = () -> {
            if (txtTailThickness.getText().length() > 0)
                mControls.setBrushSize(Float.parseFloat(txtTailThickness.getText()));
        };
    }
}
