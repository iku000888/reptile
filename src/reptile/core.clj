(ns reptile.core
  (:require [com.stuartsierra.component :as component])
  (:import [javax.swing JFrame JLabel SwingUtilities]))

(defprotocol IReptileJFrame
  (start [frame])
  (stop [frame])
  (set-width-height [frame w h]))

(defrecord ReptileJFrame [map]
  IReptileJFrame
  (start [this]
    (let [frame (JFrame. "HelloWorldSwing")
          label (JLabel. "Hello World")]
      (-> frame .getContentPane (.add label))
      (doto frame (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
            .pack
            (.setVisible true))
      (assoc this :frame frame)))
  (stop [this]
    (.dispose (:frame this))
    (dissoc this :frame))
  (set-width-height [this w h]
    (.setSize (:frame this) w h)))
