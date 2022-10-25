import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.*;
import java.text.MessageFormat;
import java.io.*;

public class score_processing_program implements ActionListener {

	JFrame frame;
	File usingFile;
	JMenuItem newFile, openFile, saveFile, saveAs, printTable;
	JLabel number, name, korean, english, math;
	JTextField student_num, student_name, sbj1, sbj2, sbj3, searchField;
	JPanel pnum, pname, psbj1, psbj2, psbj3, leftpanel, center;
	JButton addBtn, delBtn, upBtn, downBtn, basicTableBtn, searchBtn, updateBtn;
	JComboBox<String> sortComboBox;
	DefaultTableModel model;
	TableRowSorter<TableModel> tablesorter;
	JTable table;
	Vector<Vector<String>> content;
	Vector<String> column;
	boolean isSorted = false;
	boolean isUp = true;

	public score_processing_program() {
		makeGui();
	}

	// 전체적인 gui설계
	private void makeGui() {
		frame = new JFrame("성적 관리 프로그램");

		
		JMenuBar mb = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		newFile = new JMenuItem("New");
		openFile = new JMenuItem("Open");
		saveFile = new JMenuItem("Save");
		saveAs = new JMenuItem("Save as..");

		JMenu printMenu = new JMenu("Print");
		printTable = new JMenuItem("Print");

		newFile.addActionListener(this);
		openFile.addActionListener(this);
		saveFile.addActionListener(this);
		saveAs.addActionListener(this);
		printTable.addActionListener(this);

		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveAs);
		printMenu.add(printTable);
		mb.add(fileMenu);
		mb.add(printMenu);
		frame.setJMenuBar(mb);

		// JToolBar
		JToolBar toolBar = new JToolBar();
		addBtn = new JButton("추가");
		delBtn = new JButton("삭제");
		updateBtn = new JButton("수정");
		basicTableBtn = new JButton("기본표");
		searchField = new JTextField(8);
		searchBtn = new JButton("검색");

		String[] columns = { "학번", "이름", "국어", "영어", "수학", "평균", "등수" };
		sortComboBox = new JComboBox<String>(columns);
		sortComboBox.setSize(10, sortComboBox.getPreferredSize().height);
		sortComboBox.addActionListener(this);

		upBtn = new JButton("^");
		downBtn = new JButton("v");

		addBtn.addActionListener(this);
		delBtn.addActionListener(this);
		updateBtn.addActionListener(this);
		basicTableBtn.addActionListener(this);
		searchBtn.addActionListener(this);
		upBtn.addActionListener(this);
		downBtn.addActionListener(this);

		toolBar.add(addBtn);
		toolBar.add(delBtn);
		toolBar.add(updateBtn);
		toolBar.add(basicTableBtn);
		toolBar.add(sortComboBox);
		toolBar.add(upBtn);
		toolBar.add(downBtn);
		toolBar.add(searchField);
		toolBar.add(searchBtn);

		// leftPanel
		number = new JLabel("학번");
		student_num = new JTextField(9);
		pnum = new JPanel();
		pnum.add(number);
		pnum.add(student_num);


		name = new JLabel("이름");
		student_name = new JTextField(9);
		pname = new JPanel();
		pname.add(name);
		pname.add(student_name);

		korean = new JLabel("국어");
		sbj1 = new JTextField(9);
		psbj1 = new JPanel();
		psbj1.add(korean);
		psbj1.add(sbj1);

		english = new JLabel("영어");
		sbj2 = new JTextField(9);
		psbj2 = new JPanel();
		psbj2.add(english);
		psbj2.add(sbj2);

		math = new JLabel("수학");
		sbj3 = new JTextField(9);
		psbj3 = new JPanel();
		psbj3.add(math);
		psbj3.add(sbj3);
		
		student_num.addKeyListener(new MyKeyListener());
		student_name.addKeyListener(new MyKeyListener());
		sbj1.addKeyListener(new MyKeyListener());
		sbj2.addKeyListener(new MyKeyListener());
		sbj3.addKeyListener(new MyKeyListener());

		leftpanel = new JPanel();
		leftpanel.setLayout(new GridLayout(6, 1));
		leftpanel.add(pnum);
		leftpanel.add(pname);
		leftpanel.add(psbj1);
		leftpanel.add(psbj2);
		leftpanel.add(psbj3);

		// table 설정
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = frame.getContentPane();

		// 더블클릭으로 셀 수정 금지
		model = new DefaultTableModel() {
			public boolean isCellEditable(int i, int c) {
				return false;
			}
		};

		table = new JTable(model);

		tablesorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(tablesorter);

		// 칼럼 위치 변경 금지
		table.getTableHeader().setReorderingAllowed(false);
		JScrollPane tablepane = new JScrollPane(table);

		center = new JPanel(new BorderLayout());
		center.add(leftpanel, "West");

		center.add(tablepane, "Center");
		c.add(center, "Center");
		c.add(toolBar, "North");

		// 윈도우 사이즈 자동설정
		frame.pack();

		frame.setVisible(true);
	}


	// 이벤트 처리
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == addBtn) {
			add();
		} else if (event == delBtn) {
			del();
		} else if (event == updateBtn) {
			update();
		} else if (event == newFile) {
			newfile();
		} else if (event == openFile) {
			openfile();
		} else if (event == saveFile) {
			savefile();
		} else if (event == saveAs) {
			if (makeNewFile()) {
				savefile();
			}
		} else if (event == printTable) {
			print();
		} else if (event == upBtn) {
			isUp = true;
			sort();
		} else if (event == downBtn) {
			isUp = false;
			sort();
		} else if (event == basicTableBtn) {
			basicTable();
		} else if (event == searchBtn) {
			search();
		}
	}

	// 데이터 추가 메소드
	private void add() {
		if(usingFile == null) {return;}
		Vector<String> vector = new Vector<>();
		String num_text = student_num.getText();
		String name_text = student_name.getText();
		String sbj1_text = sbj1.getText();
		String sbj2_text = sbj2.getText();
		String sbj3_text = sbj3.getText();
		if (checkDuplicate(num_text)) {
			JOptionPane.showMessageDialog(null, "이미 있는 학번입니다.");
		}

		else if (checkNum(num_text)) {
			JOptionPane.showMessageDialog(null, "학번은 숫자만 입력 가능합니다.");
		}

		else if (num_text.equals("")) {
			JOptionPane.showMessageDialog(null, "학번을 입력해 주세요");
			return;
		}

		else if (name_text.equals("")) {
			JOptionPane.showMessageDialog(null, "이름을 입력해 주세요");
			return;
		}

		else if (sbj1_text.equals("")) {
			JOptionPane.showMessageDialog(null, "국어점수를 입력해 주세요");
			return;
		}

		else if (sbj2_text.equals("")) {

			JOptionPane.showMessageDialog(null, "영어점수를 입력해 주세요");
			return;
		}

		else if (sbj3_text.equals("")) {
			JOptionPane.showMessageDialog(null, "수학점수를 입력해 주세요");
		}

		else if (checkNum(sbj1_text, sbj2_text, sbj3_text)) {
			JOptionPane.showMessageDialog(null, "점수를 제대로 입력해주세요.");
		}

		else {
			vector.add(num_text);
			vector.add(name_text);
			vector.add(sbj1_text);
			vector.add(sbj2_text);
			vector.add(sbj3_text);
			model.addRow(vector);

			makeAverage();
			makeRank();

			// 정렬되어 있는 상태라면 데이터를 추가할 때 자동으로 정렬해준다.
			if (isSorted) {
				content.add(vector);
				sort();
			}

			clear();
		}
	}

	// 삭제
	private void del() {
		if (table.getSelectedRow() != -1) {
			model.removeRow(table.getSelectedRow());
		}
		makeRank();
	}

	// 수정
	private void update() {
		int index = table.getSelectedRow();

		// 선택된 행이 없을 때 index는 -1이다.
		if (index == -1) {
			return;
		}

		// 선택된 행의 첫번째 값(학번)을 content와 비교해
		// content 몇번째 줄에 해당 학번이 있는지 파악
		String str = (String) table.getValueAt(index, 0);
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i).get(0).equals(str)) {
				index = i;
			}
		}

		// JTextField에 수정할 데이터를 입력했을 땐 해당 데이터를, 아무것도 입력하지 않았을 땐 content안의 원래 데이터를 변수에 저장
		String num = ((student_num.getText().equals("")) ? content.get(index).get(0) : student_num.getText());
		String name = ((student_name.getText().equals("")) ? content.get(index).get(1) : student_name.getText());
		String subject1 = ((sbj1.getText().equals("")) ? content.get(index).get(2) : sbj1.getText());
		String subject2 = ((sbj2.getText().equals("")) ? content.get(index).get(3) : sbj2.getText());
		String subject3 = ((sbj3.getText().equals("")) ? content.get(index).get(4) : sbj3.getText());

		if (checkNum(subject1, subject2, subject3)) {
			JOptionPane.showMessageDialog(null, "점수를 제대로 입력해주세요.");
			return;
		}

		if (checkNum(num)) {
			JOptionPane.showMessageDialog(null, "점수를 제대로 입력해주세요.");
			return;
		}

		// 해당 행의 학번이 수정된 상태일 경우에
		if (!content.get(index).get(0).equals(num)) {
			// 다른 행들의 학번들과 겹치는지 확인
			if (checkDuplicate(num)) {
				

JOptionPane.showMessageDialog(null, "이미 있는 학번입니다.");
				return;
			}
		}

		Vector<String> vector = content.get(index);
		vector.setElementAt(num, 0);
		vector.setElementAt(name, 1);
		vector.setElementAt(subject1, 2);
		vector.setElementAt(subject2, 3);
		vector.setElementAt(subject3, 4);

		model.setDataVector(content, column);

		makeAverage();
		makeRank();

		clear();
	}

	// 평균(소수점은 세자리 까지만)
	private void makeAverage() {
		for (int i = 0; i < table.getRowCount(); i++) {
			float k = Integer.parseInt((String) table.getValueAt(i, 2));
			float e = Integer.parseInt((String) table.getValueAt(i, 3));
			float m = Integer.parseInt((String) table.getValueAt(i, 4));
			float average = (k + e + m) / 3;

			// 소수점 세자리 까지만
			table.setValueAt(String.valueOf(Math.round(average * 1000) / 1000.0), i, 5);
		}
	}

	// 등수
	private void makeRank() {
		Float[] array = new Float[table.getRowCount()];

		for (int i = 0; i < table.getRowCount(); i++) {
			array[i] = Float.valueOf((String) table.getValueAt(i, 5));
		}

		for (int i = 0; i < table.getRowCount(); i++) {
			int count = 1;
			for (int j = 0; j < table.getRowCount(); j++) {
				if (Float.valueOf((String) table.getValueAt(i, 5)) < Float.valueOf((String) table.getValueAt(j, 5))) {
					count++;
				}
				table.setValueAt(String.valueOf(count), i, 6);
			}
		}
	}

	// 새로운 파일 생성( 파일을 생성하지 않고 취소를 눌렀을 때 false를 리턴 )
	private boolean makeNewFile() {
		String filepath = "";
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("파일경로선택");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(frame);
		File dir = fileChooser.getSelectedFile();
		if (dir != null) {
			filepath = dir.getPath();
		} else {
			return false;
		}

		String fileName = JOptionPane.showInputDialog(null, "파일명 입력(확장자 제외)", JOptionPane.OK_CANCEL_OPTION);
		if (fileName == null) {
			return false;
		}
		filepath = filepath + "\\" + fileName + ".txt";
		usingFile = new File(filepath);
		return true;
	}

	// 새로운 파일을 만들고 파일에 Column 생성 후 JTable 생성
	private void newfile() {
		if (makeNewFile()) {
			makeColumnsInFile();
			makeBasicTable();
		}
	}

	// 기존의 파일을 열어 JTable 생성
	private void openfile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("파일선택");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt");
		fileChooser.setFileFilter(filter);
		int result = fileChooser.showOpenDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			usingFile = fileChooser.getSelectedFile();

			makeBasicTable();
		}
	}

	// 파일 저장
	private void savefile() {
		if (usingFile == null) {
			return;
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(usingFile), "utf-8"));
			bw.write("학번\t이름\t국어\t영어\t수학\t평균\t등수\n");
			for (int i = 0; i < table.getRowCount(); i++) {
				for (int j = 0; j < table.getColumnCount(); j++) {
					bw.write((String) table.getModel().getValueAt(i, j) + "\t");
				}
				bw.write("\n");
			}
			bw.close();
			JOptionPane.showMessageDialog(null, "저장완료");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 프린트
	private void print() {
		if(usingFile == null) {
			return;
		}
		String fileName = usingFile.getName();
		String[] array = fileName.split("\\.");

		try {
			table.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat(array[0]), new MessageFormat(""));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "오류발생.");
		}
	}

	// 기본 표로 전환(정렬된 상태에서 원래대로 되돌리고 싶을 때 사용)
	private void basicTable() {
		isSorted = false;
		searchField.setText("");
		search();
		model.setDataVector(content, column);
	}

	// 표를 정렬
	private void sort() {
		if (usingFile == null) {
			return;
		}
		isSorted = true;

		int index = sortComboBox.getSelectedIndex();
		if (isUp) {
			switch (index) {
			case 0:
				sortByInteger(index, true);
				break;
			case 1:
				sortByString(index, true);
				break;
			case 2:
				sortByInteger(index, true);
				break;
			case 3:
				sortByInteger(index, true);
				break;
			case 4:
				sortByInteger(index, true);
				break;
			case 5:
				sortByFloat(index, true);
				break;
			case 6:
				sortByInteger(index, true);
				break;
			}
		}

		else {
			switch (index) {
			case 0:
				sortByInteger(index, false);
				break;
			case 1:
				sortByString(index, false);
				break;
			case 2:
				sortByInteger(index, false);
				break;
			case 3:
				sortByInteger(index, false);
				break;
			case 4:
				sortByInteger(index, false);
				break;
			case 5:
				sortByFloat(index, false);
				break;
			case 6:
				sortByInteger(index, false);
				break;
			}
		}
	}

	// 문자열 기준으로 정렬, isReverse가 true 면 오름차순, false면 내림차순
	private void sortByString(int index, boolean isReverse) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<String> sortKey = new Vector<String>();
		for (int i = 0; i < content.size(); i++) {
			sortKey.add((String) content.get(i).get(index));
		}

		if (isReverse) {
			Collections.sort(sortKey, Collections.reverseOrder());
		} else {
			Collections.sort(sortKey);
		}

		Vector<Integer> num = new Vector<Integer>();
		
		for (int i = 0; i < sortKey.size(); i++) {
			for (int k = 0; k < content.size(); k++) {
				boolean pass = false;
				
				for (int j = 0; j < num.size(); j++) {
					if (num.get(j).equals(k)) {
						pass = true;
					}
				}
				if (pass) {
					continue;
				}
				
				if (sortKey.get(i).equals(content.get(k).get(index))) {
					num.add(k);
					Vector<String> arrResult = content.get(k);
					result.add(arrResult);
					break;
				}
			}
		}
		model.setDataVector(result, column);
	}

	// 정수형 기준으로 정렬
	private void sortByInteger(int index, boolean isReverse) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<Integer> sortKey = new Vector<Integer>();
		for (int i = 0; i < content.size(); i++) {
			sortKey.add(Integer.parseInt((String) content.get(i).get(index)));
		}

		if (isReverse) {
			Collections.sort(sortKey, Collections.reverseOrder());
		} else {
			Collections.sort(sortKey);
		}

		Vector<Integer> num = new Vector<Integer>();
		
		for (int i = 0; i < sortKey.size(); i++) {
			for (int k = 0; k < content.size(); k++) {
				boolean pass = false;
				
				for (int j = 0; j < num.size(); j++) {
					if (num.get(j).equals(k)) {
						pass = true;
					}
				}
				if (pass) {
					continue;
				}
				
				if (sortKey.get(i).equals(Integer.parseInt((String) content.get(k).get(index)))) {
					num.add(k);
					Vector<String> arrResult = content.get(k);
					result.add(arrResult);
					break;
				}
			}
		}
		model.setDataVector(result, column);
	}

	// 실수형 기준으로 정렬
	private void sortByFloat(int index, boolean isReverse) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		Vector<Float> sortKey = new Vector<Float>();
		for (int i = 0; i < content.size(); i++) {
			sortKey.add((float) (Math.round(Float.parseFloat((String) content.get(i).get(index))) * 1000 / 1000.0));
		}

		if (isReverse) {
			Collections.sort(sortKey, Collections.reverseOrder());
		} else {
			Collections.sort(sortKey);
		}
		
		Vector<Integer> num = new Vector<Integer>();

		for (int i = 0; i < sortKey.size(); i++) {
			for (int k = 0; k < content.size(); k++) {
				boolean pass = false;
				
				for (int j = 0; j < num.size(); j++) {
					if (num.get(j).equals(k)) {
						pass = true;
					}
				}
				if (pass) {
					continue;
				}
				
				if (sortKey.get(i).equals(
						(float) (Math.round(Float.parseFloat((String) content.get(k).get(index))) * 1000 / 1000.0))) {
					num.add(k);
					Vector<String> arrResult = content.get(k);
					result.add(arrResult);
					break;

				}
			}
		}
		model.setDataVector(result, column);
	}

	// 검색
	private void search() {
		String text = searchField.getText();
		tablesorter.setRowFilter(RowFilter.regexFilter(text));
	}

	// 파일을 불러와 기본 테이블 생성
	private void makeBasicTable() {

		column = new Vector<String>();
		content = new Vector<>();

		makeColumnsInJTable();
		if(usingFile == null) {
			return;
		}
		makeContentsInJTable();
		model.setDataVector(content, column);

		makeAverage();
		makeRank();
		frame.setTitle("성적 관리 프로그램 - " + usingFile.getName());
	}

	// 파일에 column 생성
	private void makeColumnsInFile() {
		try {
			OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(usingFile), "utf-8");
			bw.write("학번\t이름\t국어\t영어\t수학\t평균\t등수\n");
			bw.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "파일처리중 오류가 발생하였습니다.");
		}
	}

	// JTable에 column 생성
	private void makeColumnsInJTable() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(usingFile), "utf-8"));
			String firstLine = br.readLine();
			if (firstLine.equals(null)) {
				makeColumnsInFile();
			}
			
			else if(! (firstLine.equals("학번\t이름\t국어\t영어\t수학\t평균\t등수"))) {
				JOptionPane.showMessageDialog(null, "잘못된 형식의 파일입니다.");
				usingFile = null;
				return;
			}

			String[] columnsName = firstLine.split("\t");

			for (String title : columnsName) {
				column.add(title);
			}
			br.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "파일처리중 오류가 발생하였습니다.");
		}
	}

	// 텍스트 파일을 토대로 JTable에 내용 삽입
	private void makeContentsInJTable() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(usingFile), "utf-8"));
			String firstLine = br.readLine();
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] dataRow = line.split("\t");
				content.add(makeVector(dataRow));
			}
			br.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "파일처리중 오류가 발생하였습니다.");
		}
	}

	// 배열을 벡터로 전환
	private Vector<String> makeVector(String[] array) {
		Vector<String> vector = new Vector<>();
		for (String data : array) {
			vector.add(data);
		}
		return vector;
	}

	// JTextField 초기화
	private void clear() {
		student_num.setText("");
		student_name.setText("");
		sbj1.setText("");
		sbj2.setText("");
		sbj3.setText("");
	}

	// 중복된 학번이 있는지 확인
	public boolean checkDuplicate(String num_text) {
		boolean duplicate = false;
		for (int i = 0; i < table.getRowCount(); i++) {
			Vector<String> vector = (Vector<String>) content.get(i);
			if (num_text.equals((String) vector.get(0))) {
				duplicate = true;
			}
		}
		return duplicate;
	}

	// 점수 부분이 0~100사이인지 확인
	private boolean checkNum(String string1, String string2, String string3) {
		boolean isNotNum = false;
		try {
			int num1 = Integer.parseInt(string1);
			int num2 = Integer.parseInt(string2);
			int num3 = Integer.parseInt(string3);
			if (num1 > 100 || num1 < 0) {
				isNotNum = true;
			} else if (num2 > 100 || num2 < 0) {
				isNotNum = true;
			} else if (num3 > 100 || num3 < 0) {
				isNotNum = true;
			}
		} catch (NumberFormatException e) {
			isNotNum = true;
		}
		return isNotNum;
	}

	private boolean checkNum(String string1) {
		boolean isNotNum = false;
		try {
			int num1 = Integer.parseInt(string1);
		}

		catch (NumberFormatException e) {
			isNotNum = true;
		}
		return isNotNum;
	}

	class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				add();
			}
		}
	}

	public static void main(String[] args) {
		new score_processing_program();
	}

}