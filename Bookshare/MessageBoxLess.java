package Bookshare;

import java.awt.*;
import java.awt.event.*;

class MessageBoxLess extends Dialog implements ActionListener
{
   Frame client;
   public MessageBoxLess(Frame parent, String title, String message){
      super(parent, title, true); // modalless Dialog »ý¼º
      setLayout(new BorderLayout());
      Label lbl = new Label(message);
      add("Center", lbl);
      Button bt = new Button("OK");
      bt.addActionListener(this);
      add("South", bt);
      pack();
      client = parent;
   }
   public void actionPerformed(ActionEvent ae){
      client.dispose();
   }
}