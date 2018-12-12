package Bookshare;

import java.awt.*;
import java.awt.event.*;

class MessageBox extends Dialog implements ActionListener
{
   public MessageBox(Frame parent, String title, String message){
      super(parent, title, false); // modal Dialog 생성
      setLayout(new BorderLayout());
      Label lbl = new Label(message);
      add("Center",lbl);
      Button bt = new Button("OK");
      bt.addActionListener(this);
      add("South", bt);
      pack();
   }
   public void actionPerformed(ActionEvent ae){
      dispose(); // OK 버튼이 눌리면 메시지 박스를 제거한다.
   }

}
