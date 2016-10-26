(ns reptile.core
  (:import [javax.swing JFrame JLabel SwingUtilities]))

(defn start []
  (let [frame (JFrame. "HelloWorldSwing")
        label (JLabel. "Hello World")]
    (-> frame .getContentPane (.add label))
    (doto frame (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE) .pack (.setVisible true))))
