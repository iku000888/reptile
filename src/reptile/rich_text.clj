(ns reptile.rich-text
  (:import java.awt.BorderLayout
           java.awt.event.ActionListener
           [javax.swing JFrame JMenu JMenuBar JMenuItem JScrollPane JTextPane]
           javax.swing.event.CaretListener
           [javax.swing.text DefaultStyledDocument StyleContext]
           javax.swing.text.rtf.RTFEditorKit))

(def flag (atom false))
(def rtf-editor (RTFEditorKit.))

(defn create-jframe-proxy []
  (proxy [JFrame ActionListener CaretListener] []
    (actionPerformed [e]
      (prn "Actionperformed!!!" e))))

(defn create-menubar [this]
  "'this' must implement the ActionListener interface"
  (let [menu-bar (JMenuBar.)
        file-menu (JMenu. "File" true)
        new-item (JMenuItem. "New")
        open-item (JMenuItem. "Open")
        save-item (JMenuItem. "Save")
        exit-item (JMenuItem. "Exit")]
    (.add menu-bar file-menu)
    (.add file-menu new-item)
    (.addActionListener new-item this)
    (.setActionCommand new-item "newItem")
    (.add file-menu open-item)
    (.addActionListener open-item this)
    (.setActionCommand open-item "openItem")
    (.add file-menu save-item)
    (.addActionListener save-item this)
    (.setActionCommand save-item "openItem")
    (.addSeparator file-menu)
    (.add file-menu exit-item)
    (.addActionListener exit-item this)
    (.setActionCommand exit-item "openItem")
    menu-bar))

(defn constructor []
  (let [jframe (doto (create-jframe-proxy)
                 (.setTitle "TextPaneTest test")
                 (.setBounds 10 10 500 300))
        text-pane (JTextPane.)
        scroll-pane (JScrollPane. text-pane
                                  JScrollPane/VERTICAL_SCROLLBAR_ALWAYS
                                  JScrollPane/HORIZONTAL_SCROLLBAR_NEVER)
        doc (DefaultStyledDocument. (StyleContext.))
        menu-bar (create-menubar jframe)]
    (->  jframe
         (.getContentPane)
         (.add scroll-pane BorderLayout/CENTER))
    (doto text-pane
      (.setDocument doc)
      (.addCaretListener jframe))
    jframe))

;;;Use
(doto (constructor)
  .pack
  (.setVisible true)
  (.setSize 400 400))
