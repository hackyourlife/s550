package org.hackyourlife.s550.ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.hackyourlife.s550.CDImage;
import org.hackyourlife.s550.CDVirtualFloppy;
import org.hackyourlife.s550.FloppyDisk;
import org.hackyourlife.s550.SystemProgram;
import org.hackyourlife.s550.io.BEInputStream;
import org.hackyourlife.s550.io.BEOutputStream;
import org.hackyourlife.s550.io.WordInputStream;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	public static final Font MONOSPACED_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	private PatchEditor patchEditor;
	private ToneEditor toneEditor;
	private WavebankEditor waveformEditor;
	private FunctionEditor functionEditor;
	private MidiEditor midiEditor;
	private JLabel status;

	private FloppyDisk disk;

	private File cd5;
	private int lastVirtualFloppy;

	public MainWindow() {
		super("S-550 Editor");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		disk = new FloppyDisk();

		patchEditor = new PatchEditor(disk);
		toneEditor = new ToneEditor(disk);
		waveformEditor = new WavebankEditor(disk);
		functionEditor = new FunctionEditor(disk);
		midiEditor = new MidiEditor(disk);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Patch", patchEditor);
		tabs.addTab("Tone", toneEditor);
		tabs.addTab("Waveform", waveformEditor);
		tabs.addTab("MIDI", midiEditor);
		tabs.addTab("Function", functionEditor);

		status = new JLabel("READY");

		add(BorderLayout.CENTER, tabs);
		add(BorderLayout.SOUTH, status);

		FileDialog loadFloppyDialog = new FileDialog(this, "Load floppy...", FileDialog.LOAD);
		FileDialog loadDiscDialog = new FileDialog(this, "Load CD-5 disc image...", FileDialog.LOAD);
		FileDialog saveFloppyDialog = new FileDialog(this, "Save floppy...", FileDialog.SAVE);

		JMenuBar menu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem newFloppy = new JMenuItem("New");
		newFloppy.setMnemonic('N');
		newFloppy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		newFloppy.addActionListener(e -> {
			disk.copyFrom(new FloppyDisk());
			status.setText("New floppy created");
			updateViews();
		});
		JMenuItem loadFloppy = new JMenuItem("Load floppy...");
		loadFloppy.setMnemonic('L');
		loadFloppy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		loadFloppy.addActionListener(e -> {
			loadFloppyDialog.setVisible(true);
			if(loadFloppyDialog.getFile() == null) {
				return;
			}
			String filename = loadFloppyDialog.getDirectory() + loadFloppyDialog.getFile();
			try {
				loadFloppy(new File(filename));
				status.setText("Floppy image loaded from " + filename);
			} catch(IOException ex) {
				status.setText("I/O Error: " + ex.getMessage());
			}
		});
		JMenuItem loadVirtualFloppy = new JMenuItem("Load virtual floppy...");
		loadVirtualFloppy.setMnemonic('v');
		loadVirtualFloppy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		loadVirtualFloppy.addActionListener(e -> {
			try {
				lastVirtualFloppy = loadDisc(cd5, lastVirtualFloppy);
			} catch(IOException ex) {
				status.setText("I/O Error: " + ex.getMessage());
			}
		});
		loadVirtualFloppy.setEnabled(false);
		JMenuItem loadDisc = new JMenuItem("Load CD-5 disc image...");
		loadDisc.setMnemonic('C');
		loadDisc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		loadDisc.addActionListener(e -> {
			loadDiscDialog.setVisible(true);
			if(loadDiscDialog.getFile() == null) {
				return;
			}
			String filename = loadDiscDialog.getDirectory() + loadDiscDialog.getFile();
			try {
				File disc = new File(filename);
				lastVirtualFloppy = loadDisc(disc, 0);
				cd5 = disc;
				loadVirtualFloppy.setEnabled(true);
			} catch(IOException ex) {
				status.setText("I/O Error: " + ex.getMessage());
			}
		});
		JMenuItem saveFloppy = new JMenuItem("Save floppy...");
		saveFloppy.setMnemonic('S');
		saveFloppy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		saveFloppy.addActionListener(e -> {
			saveFloppyDialog.setVisible(true);
			if(saveFloppyDialog.getFile() == null) {
				return;
			}
			String filename = saveFloppyDialog.getDirectory() + saveFloppyDialog.getFile();
			try {
				saveFloppy(new File(filename));
				status.setText("Floppy image saved to " + filename);
			} catch(IOException ex) {
				status.setText("I/O Error: " + ex.getMessage());
			}
		});
		JMenuItem midiConfig = new JMenuItem("MIDI configuration...");
		midiConfig.setMnemonic('M');
		midiConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic('x');
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		exit.addActionListener(e -> System.exit(0));

		fileMenu.add(newFloppy);
		fileMenu.addSeparator();
		fileMenu.add(loadFloppy);
		fileMenu.add(loadDisc);
		fileMenu.add(loadVirtualFloppy);
		fileMenu.addSeparator();
		fileMenu.add(saveFloppy);
		fileMenu.addSeparator();
		fileMenu.add(midiConfig);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		menu.add(fileMenu);
		setJMenuBar(menu);

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	public void loadFloppy(File file) throws IOException {
		try(BEInputStream in = new BEInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			disk.read(in);
		}
		updateViews();
	}

	private static class Info {
		private final int index;
		private final String name;
		private final String upper;

		public Info(int index, String name) {
			this.index = index;
			this.name = name;
			this.upper = name.toUpperCase();
		}

		public int getIndex() {
			return index;
		}

		public boolean contains(String search) {
			return upper.contains(search.toUpperCase());
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public int loadDisc(File file, int index) throws IOException {
		byte[] disc = Files.readAllBytes(file.toPath());
		CDImage cd = new CDImage(disc);
		List<String> names = new ArrayList<>();
		List<Info> floppyNames = new ArrayList<>();
		List<Info> patchNames = new ArrayList<>();
		List<Info> toneNames = new ArrayList<>();

		for(int i = 0; i < cd.getVirtualFloppyCount(); i++) {
			names.add(String.format("[%03d] %s", i, cd.getVirtualFloppyName(i)));
			floppyNames.add(new Info(i, cd.getVirtualFloppyName(i)));
			CDVirtualFloppy floppy = cd.getFloppy(i);
			for(int j = 0; j < FloppyDisk.PATCH_COUNT; j++) {
				String name = floppy.getPatchParameter(j).getName().trim();
				if(name.length() > 0) {
					patchNames.add(new Info(i, name));
				}
			}
			for(int j = 0; j < FloppyDisk.TONE_COUNT; j++) {
				String name = floppy.getToneParameter(j).getName().trim();
				if(name.length() > 0) {
					toneNames.add(new Info(i, name));
				}
			}
		}

		if(names.isEmpty()) {
			throw new IOException("No virtual floppies contained on the disc");
		}

		JDialog dlg = new JDialog(this, "Select virtual floppy...", true);
		dlg.setLayout(new BorderLayout());

		JList<String> floppies = new JList<>(names.toArray(new String[names.size()]));
		floppies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		floppies.setSelectedIndex(0);
		floppies.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					dlg.dispose();
				}
			}
		});
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		floppies.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, enter);
		floppies.getActionMap().put(enter, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.dispose();
			}
		});
		floppies.setSelectedIndex(index);

		JTextField floppyNameFilter = new JTextField();
		JTextField patchNameFilter = new JTextField();
		JTextField toneNameFilter = new JTextField();
		DefaultListModel<Info> floppyResult = new DefaultListModel<>();
		DefaultListModel<Info> patchResult = new DefaultListModel<>();
		DefaultListModel<Info> toneResult = new DefaultListModel<>();
		JList<Info> floppyFilterResult = new JList<>(floppyResult);
		JList<Info> patchFilterResult = new JList<>(patchResult);
		JList<Info> toneFilterResult = new JList<>(toneResult);

		floppyFilterResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		patchFilterResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		toneFilterResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		floppyNameFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				floppyResult.clear();
				floppyFilterResult.clearSelection();
				String text = floppyNameFilter.getText().trim();
				if(text.length() == 0) {
					return;
				}
				for(Info floppyName : floppyNames) {
					if(floppyName.contains(text)) {
						floppyResult.addElement(floppyName);
					}
				}
			}
		});

		patchNameFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				patchResult.clear();
				String text = patchNameFilter.getText().trim();
				if(text.length() == 0) {
					return;
				}
				for(Info patchName : patchNames) {
					if(patchName.contains(text)) {
						patchResult.addElement(patchName);
					}
				}
			}
		});

		toneNameFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				toneResult.clear();
				String text = toneNameFilter.getText().trim();
				if(text.length() == 0) {
					return;
				}
				for(Info toneName : toneNames) {
					if(toneName.contains(text)) {
						toneResult.addElement(toneName);
					}
				}
			}
		});

		floppyFilterResult.addListSelectionListener(e -> {
			Info info = floppyFilterResult.getSelectedValue();
			if(info == null) {
				return;
			} else {
				int i = info.getIndex();
				floppies.setSelectedIndex(i);
				floppies.ensureIndexIsVisible(i);
			}
		});

		patchFilterResult.addListSelectionListener(e -> {
			Info info = patchFilterResult.getSelectedValue();
			if(info == null) {
				return;
			} else {
				int i = info.getIndex();
				floppies.setSelectedIndex(i);
				floppies.ensureIndexIsVisible(i);
			}
		});

		toneFilterResult.addListSelectionListener(e -> {
			Info info = toneFilterResult.getSelectedValue();
			if(info == null) {
				return;
			} else {
				int i = info.getIndex();
				floppies.setSelectedIndex(i);
				floppies.ensureIndexIsVisible(i);
			}
		});

		JPanel floppyFilter = new JPanel(new BorderLayout());
		floppyFilter.add(BorderLayout.CENTER, new JScrollPane(floppyFilterResult));
		floppyFilter.add(BorderLayout.SOUTH, floppyNameFilter);

		JPanel patchFilter = new JPanel(new BorderLayout());
		patchFilter.add(BorderLayout.CENTER, new JScrollPane(patchFilterResult));
		patchFilter.add(BorderLayout.SOUTH, patchNameFilter);

		JPanel toneFilter = new JPanel(new BorderLayout());
		toneFilter.add(BorderLayout.CENTER, new JScrollPane(toneFilterResult));
		toneFilter.add(BorderLayout.SOUTH, toneNameFilter);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Floppy", floppyFilter);
		tabs.addTab("Patch", patchFilter);
		tabs.addTab("Tone", toneFilter);

		JPanel south = new JPanel(new FlowLayout());
		JButton ok = new JButton("Load");
		ok.addActionListener(e -> dlg.dispose());
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e -> {
			floppies.clearSelection();
			dlg.dispose();
		});
		south.add(ok);
		south.add(cancel);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setLeftComponent(new JScrollPane(floppies));
		split.setRightComponent(tabs);
		split.setResizeWeight(0.6);
		dlg.add(BorderLayout.CENTER, split);
		dlg.add(BorderLayout.SOUTH, south);
		dlg.setSize(500, 400);
		dlg.setLocationRelativeTo(null);
		floppies.ensureIndexIsVisible(floppies.getSelectedIndex());
		dlg.setVisible(true);

		int selected = floppies.getSelectedIndex();
		if(selected == -1) {
			status.setText("No virtual floppy loaded");
			return index;
		} else {
			CDVirtualFloppy floppy = cd.getFloppy(selected);
			byte[] data;
			try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				floppy.writeFloppy(new BEOutputStream(out), new SystemProgram());
				out.flush();
				data = out.toByteArray();
			}
			try(WordInputStream in = new BEInputStream(new ByteArrayInputStream(data))) {
				disk.read(in);
			}
		}
		status.setText("CD-5 disc image loaded from " + file + " [#" + selected + ": " +
				cd.getVirtualFloppyName(selected) + "]");
		updateViews();
		return selected;
	}

	public void saveFloppy(File file) throws IOException {
		try(BEOutputStream out = new BEOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			disk.write(out);
		}
	}

	private void updateViews() {
		patchEditor.update();
		toneEditor.update();
		waveformEditor.update();
		functionEditor.update();
		midiEditor.update();
	}

	public static void main(String[] args) {
		MainWindow w = new MainWindow();
		w.setVisible(true);
	}
}
