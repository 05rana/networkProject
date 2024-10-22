/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.phase1;
import javax.swing.*;

public class wait extends javax.swing.JFrame {

    private String name;
    
    public wait(String userName) {
        this.name = userName;

        // إعداد الفريم
        setTitle("Waiting Room");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // عرض الاسم وقائمة الأعضاء
        JLabel welcomeLabel = new JLabel("Welcome " + name + ", you are in the Waiting Room.");
        welcomeLabel.setBounds(50, 50, 300, 20);
        add(welcomeLabel);

        JLabel membersLabel = new JLabel("Members in the room:");
        membersLabel.setBounds(50, 80, 150, 20);
        add(membersLabel);

        // قائمة الأعضاء
        JTextArea membersList = new JTextArea();
        membersList.setBounds(50, 110, 200, 100);
        // هنا يجب أن يتم تحديث القائمة من السيرفر أو البيانات الفعلية
        membersList.setText("Member 1\nMember 2\n...");
        add(membersList);
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
