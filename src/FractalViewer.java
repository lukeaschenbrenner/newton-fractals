import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ProgressMonitor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class FractalViewer extends JPanel implements ActionListener, PropertyChangeListener {

	static final int DEFAULT_WIDTH = 890;
	static final int DEFAULT_HEIGHT = 700;

	public static JScrollPane previewScrollPane;

	public static JTextField z0Field, z1Field, z2Field, z3Field, z4Field, z5Field, z6Field, z7Field, z8Field, z9Field;
	public static JButton genButton, printButton;
	public static ProgressMonitor progressMonitor;
	public NewtonFractal operation;

	double[] coefficients;

	public FractalViewer() {
		coefficients = new double[10];

		JPanel settingsPanel = new JPanel();
		settingsPanel.setFocusable(true);

		JLabel equationFieldLabel = new JLabel("Your name:");
		settingsPanel.add(equationFieldLabel);


		z9Field = new JTextField(20);
		z9Field.setText("");
		z9Field.setHorizontalAlignment(JTextField.RIGHT);
		//JLabel z9Label = new JLabel("z⁹ +");
		settingsPanel.add(z9Field);
	//	settingsPanel.add(z9Label);

/*		z8Field = new JTextField(2);
		z8Field.setText("0");
		z8Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z8Label = new JLabel("z⁸ +");
		settingsPanel.add(z8Field);
		settingsPanel.add(z8Label);

		z7Field = new JTextField(2);
		z7Field.setText("0");
		z7Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z7Label = new JLabel("z⁷ +");
		settingsPanel.add(z7Field);
		settingsPanel.add(z7Label);

		z6Field = new JTextField(2);
		z6Field.setText("0");
		z6Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z6Label = new JLabel("z⁶ +");
		settingsPanel.add(z6Field);
		settingsPanel.add(z6Label);

		z5Field = new JTextField(2);
		z5Field.setText("0");
		z5Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z5Label = new JLabel("z⁵ +");
		settingsPanel.add(z5Field);
		settingsPanel.add(z5Label);

		z4Field = new JTextField(2);
		z4Field.setText("0");
		z4Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z4Label = new JLabel("z⁴ +");
		settingsPanel.add(z4Field);
		settingsPanel.add(z4Label);

		z3Field = new JTextField(2);
		z3Field.setText("1");
		z3Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z3Label = new JLabel("z³ +");
		settingsPanel.add(z3Field);
		settingsPanel.add(z3Label);

		z2Field = new JTextField(2);
		z2Field.setText("0");
		z2Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z2Label = new JLabel("z² +");
		settingsPanel.add(z2Field);
		settingsPanel.add(z2Label);

		z1Field = new JTextField(2);
		z1Field.setText("0");
		z1Field.setHorizontalAlignment(JTextField.RIGHT);
		JLabel z1Label = new JLabel("z +");
		settingsPanel.add(z1Field);
		settingsPanel.add(z1Label);

		z0Field = new JTextField(2);
		z0Field.setText("-1");
		z0Field.setHorizontalAlignment(JTextField.RIGHT);
		settingsPanel.add(z0Field);

 */

		genButton = new JButton("Generate");
		genButton.setActionCommand("generate");
		genButton.addActionListener(this);

		settingsPanel.add(genButton);

		printButton = new JButton("Print\uD83D\uDDA8");
		printButton.setActionCommand("print");
		printButton.addActionListener(this);

		settingsPanel.add(printButton);

		previewScrollPane = new JScrollPane();

		setLayout(new BorderLayout(10, 0));
		JScrollPane topScrollPanel = new JScrollPane(settingsPanel);

		this.add(topScrollPanel, BorderLayout.PAGE_START);
		this.add(previewScrollPane, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if ("generate".equals(event.getActionCommand())) {
			try {
				char[] nameChars = z9Field.getText().toLowerCase().toCharArray();
				if(nameChars.length > 10){
					for(int i = 10; i < nameChars.length; i++){
						nameChars[i % 10] = (Character.toLowerCase((char)(nameChars[i % 10] + nameChars[i])));
					}
				}

				for(int i = 0; i < 10 && i < nameChars.length; i++){
					//coefficients[i] = (double)(((nameChars[i])-96.0) % 50);
					coefficients[i] = (double)(((nameChars[i])-110.0) % 50);
				}
				for(int i = nameChars.length; i < 10; i++){
					coefficients[i] = 0;
				}
				if(nameChars.length <= 3){
					coefficients[4] = coefficients[0];
					coefficients[5] = coefficients[0];
					coefficients[6] = coefficients[0];
				}


				System.out.println(Arrays.toString(coefficients));
				double absCoeffSum = 0;
				for (int i = 9; i > 1; i--) {
					absCoeffSum += Math.abs(coefficients[i]);
				}

				if (absCoeffSum == 0) {
					JOptionPane.showMessageDialog(this,
							"The polynomial must be of at least order 2, ie include a Z² or higher term",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				progressMonitor = new ProgressMonitor(FractalViewer.this, "Generating fractal...\uD83C\uDF83\uD83C\uDF83\uD83C\uDF83", "",0, 100);
				progressMonitor.setProgress(0);

		//		operation = new NewtonFractal(DEFAULT_WIDTH-4, DEFAULT_WIDTH-65, new Polynomial(coefficients));
				operation = new NewtonFractal(1100, 850, new Polynomial(coefficients));
				operation.addPropertyChangeListener(this);
				operation.execute();

				setFieldsEnabled(false);

			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
						"You should input a valid number (" + e + ")",
						"Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if("print".equals(event.getActionCommand())){
			try {
				new Thread(new PrintActionListener(operation.get())).start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"The printer execution failed. Luke is sorry.",
						"Error", JOptionPane.ERROR_MESSAGE);			}
			catch (NullPointerException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						"You can't print a blank image!",
						"Error", JOptionPane.ERROR_MESSAGE);			}
		}
	}


	public static void setFieldsEnabled(boolean b) {
/*
		z0Field.setEnabled(b);
		z1Field.setEnabled(b);
		z2Field.setEnabled(b);
		z3Field.setEnabled(b);
		z4Field.setEnabled(b);
		z5Field.setEnabled(b);
		z6Field.setEnabled(b);
		z7Field.setEnabled(b);
		z8Field.setEnabled(b); */
		z9Field.setEnabled(b);

		genButton.setEnabled(b);
		printButton.setEnabled(b);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (progressMonitor.isCanceled()) {
			operation.cancel(true);
		} else if (event.getPropertyName().equals("progress")) {
				int progress = ((Integer)event.getNewValue()).intValue();
				progressMonitor.setProgress(progress);
		}

		if (operation.isDone()) {
			try {

				previewScrollPane.getViewport().add(new JLabel(new ImageIcon(operation.get())));
			} catch (CancellationException e) {
				// Fails silently
				/*
				JOptionPane.showMessageDialog(this,
						"Couldn't display the generated image (" + e + ")",
						"Error", JOptionPane.ERROR_MESSAGE);
				*/
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void centreWindow(Window frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Spooky Science Bash 2022 - Newton Fractal Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		JComponent contentPane = new FractalViewer();
		contentPane.setOpaque(true);
		frame.setContentPane(contentPane);
		centreWindow(frame);

		frame.setVisible(true);
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
