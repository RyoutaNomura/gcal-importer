# Gcal-importer
GCal-importer is batch program for updating Google calender with external calendar(like Ariel)

* Create new Google Calendar's Event Calendar when new schedule is added to external calendar
* Update or delete Google Calendar's Event which was created by external calendar according to external change

# Usage

1. Create setting.json file and place it on the path you like (Please refer setting.json.sample)
2. Call com.rn.tool.Main#exec with -f <setting.json path>
