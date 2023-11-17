package Food_Log;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class HashTag extends JFrame {
	private static JPanel panel;
	private static JPanel p_list;
	private static JButton btnBack;
	private static ImageIcon Back = new ImageIcon("./img/Icon_Back.png");
	private static JLabel title;
	private static JPanel p_search;
	private static JButton btn_search;
	private static JTextField search;
	private static JList<String> list;
	private static Vector<String> data;
	private static DefaultListModel<String> listModel;
	private static JScrollPane scrollPane;
	private static int data_idx = 0;

	// 해시태그 검색하기
	HashTag() {
		setTitle("해시태그 검색하기");
		setSize(900, 600);
		setResizable(false);
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.white);

		list();

		setVisible(true);
	}

	public void list() {
		// 배경 패널
		panel = new JPanel();
		panel.setBounds(0, 0, 900, 600);
		panel.setBackground(Color.white);

		// 뒤로가기 버튼
		btnBack();

		// 제목을 담을 JLabel 생성
		title = new JLabel("태그 검색");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font("땅스부대찌개 Bold", Font.PLAIN, 40));
		title.setBackground(Color.white);
		title.setOpaque(true); //투명하게

		//검색창
		String searchText = "태그를 입력하세요 ('#' 없이 입력)";
		p_search = new JPanel();
		p_search.setBounds(175, 115, 550, 40);
		p_search.setBackground(Color.WHITE);

		search = new JTextField(searchText);
		search.setFont(new Font("EF_watermelonSalad", Font.PLAIN, 30));

		// 검색창 클릭 시 "검색할 태그를 입력하세요" 글씨 제거
		search.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				if(search.getText().isEmpty()) {
					search.setText(searchText);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				if(search.getText().equals(searchText)) {
					search.setText("");
				}
			}
		});

		btn_search = new JButton("검색");
		btn_search.setFont(new Font("EF_watermelonSalad", Font.PLAIN, 30));
		btn_search.setBounds(700, 115, 50, 40);
		btn_search.setBackground(Color.white);
		btn_search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String text = search.getText();
					List_data l = new List_data();
					String sql = "SELECT * FROM food_log." + l.user_id + " WHERE food_hash LIKE '%#" + text + "%'";
					new List_data(sql);
					data.clear();
					while(l.rs.next()) {
						data.add(l.getFoodName() + "  |  " + l.getFoodHash());
					}
					updateList();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		p_search.add(search);
		p_search.add(btn_search);

		// JList에 표시할 데이터 생성
		data = new Vector<String>();
		listDB();

		// 리스트 생성
		createList();

		// 위치 조정
		panel.setBorder(new EmptyBorder(60, 0, 70, 0));

		// JScrollPane 생성 및 JList를 JScrollPane에 추가
		scrollPane = new JScrollPane(list);
		// 크기 조정
		scrollPane.setPreferredSize(new Dimension(550,350));

		// JPanel을 생성하여 제목과 JScrollPane를 추가
		p_list = new JPanel(new BorderLayout());

		p_list.add(title, BorderLayout.NORTH);
		p_list.add(p_search, BorderLayout.CENTER);
		p_list.add(scrollPane, BorderLayout.SOUTH);

		// 화면 추가
		add(btnBack);
		panel.add(p_list);
		add(panel);

		// 프레임을 표시
		setVisible(true);
	
	}
	
	// 리스트 생성 메소드
	public void createList() {
		// JList 생성
		listModel = new DefaultListModel<>();
		list = new JList<>(listModel);
		
		// 칸 간격 조정
		list.setFixedCellHeight(40);
		list.setFont(new Font("EF_watermelonSalad", Font.PLAIN, 20));

		list.addListSelectionListener(new ListSelectionListener() {  // 리스트 선택 이벤트
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				data_idx = list.getSelectedIndex() + 1;
			}
		});

	}
	
	public void updateList() {
		listModel.clear();
		for(String i : data) {
			listModel.addElement(i);
		}
	}

	public void listDB() {
		try {
			List_data l = new List_data();
			while(l.rs.next()) {
				data.add(l.getFoodName() + "  |  " + l.getFoodHash());
			}
			System.out.println("DB 호출 성공");
		}catch(Exception e) {

		}
	}
	
	// 뒤로가기 버튼
	public void btnBack() {
		btnBack = new JButton(Back);
		btnBack.setBounds(15, 15, 50, 50);
		btnBack.setBackground(Color.white);
		btnBack.setFocusPainted(false);
		btnBack.setBorderPainted(false);
		
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Select();
				setVisible(false);
				data.clear();  // 리스트 재생성 방지
			}
		});
	}

	public static void main(String[] args) {
		new HashTag();
	}
}
