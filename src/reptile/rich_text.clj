(ns reptile.rich-text
  (:import [java.awt BorderLayout Color Dimension FlowLayout GraphicsEnvironment]
           java.awt.event.ActionListener
           [javax.swing DefaultComboBoxModel JComboBox JFrame JMenu JMenuBar JMenuItem JScrollPane JTextPane JToggleButton JToolBar]
           javax.swing.event.CaretListener
           [javax.swing.text BadLocationException DefaultStyledDocument SimpleAttributeSet StyleConstants StyleContext]
           javax.swing.text.rtf.RTFEditorKit)
  (:require [clojure.string :as str]))

(def rtf-editor (RTFEditorKit.))

(definterface IStateHolder
  (getStateMap [])
  (swapStateMap [update-fn]))

;; anctionPerformed handlers
(defmulti handle-action (fn [_ cmd] (.getActionCommand cmd)))

(defmethod handle-action :default
  [state e]
  (prn "Dunno whaaat to do"))

(defmethod handle-action "exitItem"
  [state e]
  (prn  state "Exititem!!!!"))

(defmethod handle-action "saveItem"
  [state e]
  (prn state "Saveitem!!!!"))

(defn set-attribute-set [document text-pane attr]
  (let [start (.getSelectionStart text-pane)
        end (.getSelectionEnd text-pane)]
    (prn "setting attribute set...")
    (.setCharacterAttributes document start (- end start) attr false)))

(defmethod handle-action "comboFonts"
  [this e]
  (let [{:keys [combo-fonts text-pane document] :as state}
        (.getStateMap this)
        attr (SimpleAttributeSet.)
        font-name (-> combo-fonts .getSelectedItem .toString)]
    (StyleConstants/setFontFamily attr font-name)
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

(defmethod handle-action "comboSizes"
  [this e]
  (prn "comboSizes" (-> this .getStateMap :combo-sizes .getSelectedItem))
  (let [{:keys [combo-sizes text-pane document]} (.getStateMap this)
        attr (SimpleAttributeSet.)
        font-size (-> combo-sizes .getSelectedItem Integer/parseInt)]
    (StyleConstants/setFontSize attr font-size)
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

(def colors ["000000" "0000FF" "00FF00" "00FFFF" "FF0000" "FF00FF" "FFFF00" "FFFFFF"])
(defmethod handle-action "combo-color"
  [this e]
  (let [{:keys [combo-color text-pane document]} (.getStateMap this)
        attr (SimpleAttributeSet.)
        color (get colors (.getSelectedIndex combo-color))
        b (Integer/parseInt (.substring color 4 6) 16)
        g (Integer/parseInt (.substring color 2 4) 16)
        r (Integer/parseInt (.substring color 0 2) 16)]
    (StyleConstants/setForeground attr (Color. r g b))
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

(defmethod handle-action "toggle-bold"
  [this e]
  (let [{:keys [toggle-bold text-pane document]} (.getStateMap this)
        attr (SimpleAttributeSet.)]
    (StyleConstants/setBold attr (.isSelected toggle-bold))
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

(defmethod handle-action "toggle-italics"
  [this e]
  (let [{:keys [toggle-italics text-pane document]} (.getStateMap this)
        attr (SimpleAttributeSet.)]
    (StyleConstants/setItalic attr (.isSelected toggle-italics))
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

(defmethod handle-action "toggle-underline"
  [this e]
  (let [{:keys [toggle-underline text-pane document]} (.getStateMap this)
        attr (SimpleAttributeSet.)]
    (StyleConstants/setUnderline attr (.isSelected toggle-underline))
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

(defmethod handle-action "toggle-strike"
  [this e]
  (let [{:keys [toggle-strike text-pane document]} (.getStateMap this)
        attr (SimpleAttributeSet.)]
    (StyleConstants/setStrikeThrough attr (.isSelected toggle-strike))
    (set-attribute-set document text-pane attr)
    (.requestFocusInWindow text-pane)))

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
        combo-sizes (JComboBox. (into-array ["8" "9" "10" "11" "12" "14" "16" "18" "20" "22" "24" "26" "28" "36" "48" "60" "72" "84" "96"]))
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
    (doseq [color colors
            :let [html (str "<html><font color=\"#" color "\">■</font></html>")]]
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
      (.add combo-color))
    {:combo-fonts combo-fonts
     :combo-sizes combo-sizes
     :combo-color combo-color
     :toggle-bold toggle-bold
     :toggle-italics toggle-italics
     :toggle-strike toggle-strike
     :toggle-underline toggle-underline}))

(defn create-jframe-proxy []
  (let [is-caret-update-atom (atom false)
        state (atom {})]
    (proxy [JFrame ActionListener CaretListener IStateHolder] []

      (actionPerformed [e]
        (when-not @is-caret-update-atom
          (handle-action this e)))

      ;; IStateHolder ops
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
        tool-bar (JToolBar.)
        tool-bar-components (init-toolbar jframe tool-bar)]
    (->  jframe
         (.getContentPane)
         (.add scroll-pane BorderLayout/CENTER))
    (doto text-pane
      (.setDocument doc)
      (.addCaretListener jframe))
    (.setJMenuBar jframe menu-bar)
    (init-document doc sc)
    (->  jframe
         (.getContentPane)
         (.add tool-bar BorderLayout/NORTH))
    (.swapStateMap jframe #(merge % {:text-pane text-pane
                                     :scroll-pane scroll-pane
                                     :style-context sc
                                     :document doc
                                     :menu-bar menu-bar
                                     :tool-bar tool-bar}
                                  tool-bar-components))
    jframe))

;;;Use
(doto (constructor)
  (.setVisible true)
  (.setSize 800 400))
