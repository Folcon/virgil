(ns lein-virgil.plugin
  (:require
   [leiningen.core.eval :as eval]))

(def overwrites
  '[[virgil "0.1.10"]
    [org.ow2.asm/asm "9.4"]
    [org.clojure/tools.namespace "1.4.1"]])

(defn overwrite-dependencies [deps overwrites]
  (let [project->dep (zipmap (map first deps) deps)]
    (->> overwrites
      (reduce
        (fn [m [p v]] (assoc m p [p v]))
        project->dep)
      vals
      vec)))

(defn middleware [project]
  (let [injections `((require 'virgil)
                     (virgil/watch ~@(:java-source-paths project)))]
   (if (contains? project :java-source-paths)
     (-> project
       (update-in [:dependencies] overwrite-dependencies overwrites)
       (update-in [:injections] concat injections))
     project)))
