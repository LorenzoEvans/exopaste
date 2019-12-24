(ns exopaste.store
    (:require [com.stuartsierra.component :as component]))

(defrecord InMemoryStore
    component/Lifecycle
    (start [this]
        (assoc this :data (atom {})))
    (stop [this] this))

(defn make-store []
    (map->InMemoryStore {}))

(defn add-new-paste [store content]
    (let [uuid (.toString (java.util.UUID/randomUUID))]
        (swap! (:data store) assoc (keyword uuid) {:content content}) uuid))

(defn get-paste-by-uuid [store uuid]
    ((keyword uuid) @(:data store)))
