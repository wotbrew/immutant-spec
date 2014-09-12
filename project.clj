(defproject immutant-spec "0.1.0"
  :description "Reference immutant resources as data"
  :url "http://github.com/danstone/immutant-spec"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies
                    [[org.immutant/immutant "1.1.0" :exclusions [common-codec]]]}}
  :immutant {:nrepl-port 4112
             :resolve-dependencies true})
