(ns reptile.sample-text-editor
  (:import [javax.swing JFrame JTextPane JScrollPane]))

(defn test []
  (let [frame (JFrame.)
        text-pane (JTextPane.)
        scroll (JScrollPane. text-pane
                             JScrollPane/VERTICAL_SCROLLBAR_ALWAYS
                             JScrollPane/HORIZONTAL_SCROLLBAR_NEVER)]
    (doto frame
      (.setTitle "TextPaneTest Test")
      (.setBounds 10 10 300 300))
    (->  frame .getContentPane (.add scroll))
    frame))

(defn main []
  (let [inst (test)]
    (doto inst
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (.setVisible true))))
