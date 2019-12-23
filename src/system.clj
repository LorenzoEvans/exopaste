(ns exopaste.system
    (:require [com.stuartsierra.component :as component]
              [exopaste.server :as server]
              [exopaste.store :as store]
              [clojure.tools.logging :refer [error]]))

(defn build-system
    "defines our system map"
    []
    (try (-> (component/system-map :server (server/make-server)
                                   :store (store/make-store))
             (component/system-using {:server [:store]}))
         (catch Exception e 
            (error "Failed to build system" e))))    

; This allows us to tell our component system, which components to use.
; We can build simple components, such as in memory store, which can be swapped,
; for real databases as backends.

; System map describes the list of components we have, and system using
; defines the relationships between them.

; In order to serve pastes, we need a server component.

; We need a url to paste to, a url to share them, and a top level form.

; The simplest version wraps the aleph server.