(ns htmx.render.period-selector
  (:require
    [htmx.util :as util]))

(defn- select
  ([label options] (select label options nil))
  ([label options value]
   [:div.col
    [:label label]
    [:select.form-control
     (util/filter-vals
       {:value value})
     [:option "Please Select"]
     (for [option options]
       [:option {:value option} option])]]))

(defn- input [title]
  [:div.col
   [:label title]
   [:input.form-control
    {:type "text"
     :placeholder title
     ;:value value
     }]])

(def months '("January"
               "February"
               "March"
               "April"
               "May"
               "June"
               "July"
               "August"
               "September"
               "October"
               "November"
               "December"))
(def months-not-specified (conj months "Not Specified"))

;; (defn period-selector []
;;   [:div
;;    [:div.row {:style "marginTop: 15px"}
;;     (select "From Month" months-not-specified)
;;     (input "From Year")]
;;    [:div.row
;;   <div>
;;                 <div style={{marginTop: '15px'}} className="row">
;;                     {this.select('From Month', notSpecifiedMonths, 'fromMonth', 'Select Month')}
;;                     {this.input('number', 'From Year', 'fromYear', 'Enter Year')}
;;                 </div>
;;                 <div style={{marginTop: '15px'}} className="row">
;;                     <div className="col">
;;                         <label>To Month {this.requiredStar}</label>
;;                         <input
;;                             type="checkbox"
;;                             checked={present || false}
;;                             onChange={() => this.togglePresent()}
;;                             className="form-check form-check-inline"
;;                         />
;;                         Present
;;                         <select
;;                             className="custom-select"
;;                             value={toMonth}
;;                             onChange={(e: SyntheticEvent<any>) => this.assocState('toMonth', e.currentTarget.value)}
;;                             disabled={present}
;;                         >
;;                             <option value={-2}>Please Select</option>
;;                             {notSpecifiedMonths.map((option, i) => <option value={i - 1} key={option}>{option}</option>)}
;;                         </select>
;;                         {this.error('toMonth', 'Select Month')}
;;                     </div>
;;                     {this.input('number', 'To Year', 'toYear', 'Enter Year', present)}
;;                 </div>
;;             </div>
