(ns reptile.rich-text
  (:import [java.awt BorderLayout Dimension FlowLayout GraphicsEnvironment]
           java.awt.event.ActionListener
           [javax.swing DefaultComboBoxModel JComboBox JFrame JMenu JMenuBar JMenuItem JScrollPane JTextPane JToggleButton JToolBar]
           javax.swing.event.CaretListener
           [javax.swing.text BadLocationException DefaultStyledDocument StyleContext]
           javax.swing.text.rtf.RTFEditorKit))

(def rtf-editor (RTFEditorKit.))

(definterface IStateHolder
  (getStateMap [])
  (swapStateMap [update-fn]))

;; anctionPerformed handlers
(defmulti handle-action #(.getActionCommand %))

(defmethod handle-action :default
  [e]
  (prn "Dunno whaaat to do"))

(defmethod handle-action "exitItem"
  [e]
  (prn "Exititem!!!!"))

(defmethod handle-action "saveItem"
  [e]
  (prn "Saveitem!!!!"))

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
    (.setActionCommand save-item "saveItem")
    (.addSeparator file-menu)
    (.add file-menu exit-item)
    (.addActionListener exit-item this)
    (.setActionCommand exit-item "exitItem")
    menu-bar))

(defn init-document [doc sc]
  (let [sb (str "スタイル付きのテキストサンプルです。\n"
                "スタイルを変えて表示しています。")]
    (try (.insertString doc 0 sb (.getStyle sc StyleContext/DEFAULT_STYLE))
         (catch BadLocationException ble
           (prn "初期文書の読み込みに失敗しました。")))))

(defn init-toolbar [jframe tool-bar]
  (let [ge (GraphicsEnvironment/getLocalGraphicsEnvironment)
        family-name (.getAvailableFontFamilyNames ge)
        combo-fonts (JComboBox. family-name)
        combo-sizes (JComboBox. (into-array ["8" "9" "10" "11" "12" "14" "16" "18" "20" "22" "24" "26" "28" "36" "48" "72"]))
        toggle-bold (JToggleButton. "<html><b>B</b></html>")
        toggle-italics (JToggleButton. "<html><i>I</i></html>")
        toggle-underline (JToggleButton. "<html><u>U</u></html>")
        toggle-strike (JToggleButton. "<html><s>S</s></html>")
        color-model (DefaultComboBoxModel.)
        combo-color (JComboBox. color-model)]
    (doto combo-fonts
      (.setMaximumSize (.getPreferredSize combo-fonts))
      (.addActionListener jframe)
      (.setActionCommand "comboFonts"))
    (doto combo-sizes
      (.setMaximumSize (.getPreferredSize combo-sizes))
      (.addActionListener jframe)
      (.setActionCommand "comboSizes"))
    (doto toggle-bold
      (.setPreferredSize (Dimension. 26 26))
      (.addActionListener jframe)
      (.setActionCommand "toggle-bold"))
    (doto toggle-italics
      (.setPreferredSize (Dimension. 26 26))
      (.addActionListener jframe)
      (.setActionCommand "toggle-italics"))
    (doto toggle-underline
      (.setPreferredSize (Dimension. 26 26))
      (.addActionListener jframe)
      (.setActionCommand "toggle-underline"))
    (doto toggle-strike
      (.setPreferredSize (Dimension. 26 26))
      (.addActionListener jframe)
      (.setActionCommand "toggle-strike"))
    (doseq [color ["#000000" "#0000FF" "#00FF00" "#00FFFF" "#FF0000" "#FF00FF" "#FFFF00" "#FFFFFF"]
            :let [html (str "<html><font color=\"" color "\">■</font></html>")]]
      (.addElement color-model html))
    (doto combo-color
      (.setMaximumSize (.getPreferredSize combo-color))
      (.addActionListener jframe)
      (.setActionCommand "combo-color"))
    (doto tool-bar
      (.setLayout (FlowLayout. FlowLayout/LEFT))
      (.add combo-fonts)
      (.add combo-sizes)
      .addSeparator
      (.add toggle-bold)
      (.add toggle-italics)
      (.add toggle-underline)
      (.add toggle-strike)
      .addSeparator
      (.add combo-color))))

(defn create-jframe-proxy []
  (let [is-caret-update-atom (atom false)
        state (atom {})]
    (proxy [JFrame ActionListener CaretListener IStateHolder] []
      (actionPerformed [e]
        (when-not @is-caret-update-atom
          (handle-action e)))
      (getStateMap [] @state)
      (swapStateMap [update-fn]
        (swap! state update-fn)))))

(defn constructor []
  (let [jframe (doto (create-jframe-proxy)
                 (.setTitle "TextPaneTest test")
                 (.setBounds 10 10 500 300))
        text-pane (JTextPane.)
        scroll-pane (JScrollPane. text-pane
                                  JScrollPane/VERTICAL_SCROLLBAR_ALWAYS
                                  JScrollPane/HORIZONTAL_SCROLLBAR_NEVER)
        sc (StyleContext.)
        doc (DefaultStyledDocument. )
        menu-bar (create-menubar jframe)
        tool-bar (JToolBar.)]
    (->  jframe
         (.getContentPane)
         (.add scroll-pane BorderLayout/CENTER))
    (doto text-pane
      (.setDocument doc)
      (.addCaretListener jframe))
    (.setJMenuBar jframe menu-bar)
    (init-document doc sc)
    (init-toolbar jframe tool-bar)
    (->  jframe
         (.getContentPane)
         (.add tool-bar BorderLayout/NORTH))
    jframe))

;;;Use
(doto (constructor)
  (.setVisible true)
  (.setSize 800 400))
