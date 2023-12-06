# SafeRideApp
CS2063 group project- Safe Ride app (Driver Interface)

## Overview

Safe Ride App is a mobile application designed for drivers providing transit services on university campuses. This README file provides essential information for users, developers, and contributors.

## Features

- **Driver Interface:** The app focuses on the driver interface, allowing drivers to manage their schedules, view student waitlists, and navigate efficiently.
- **View Schedule:** Drivers can view their weekly schedule, including day, date, timings, and location of work.
- **Active Shift Toggle:** Drivers can toggle their active shift status, indicating when they are actively working.
- **Location Selection:** Drivers can select their current working location from a list and view students on the waitlist at that location.
- **Remove Student:** Drivers can remove students from the waitlist once they have been dropped.
- **Student Info:** Detailed information about a student, including student number, name, drop-off location, and an option to open the location in Google Maps.
- **Weather Info:** A notification button (cloud-sun icon) displays weather information, helping drivers prepare for harsh weather conditions.
- **Logout:** Convenient logout button for drivers.

## Setup

1. Clone the repository: `git clone https://github.com/TinaKashwani/SafeRideApp.git`
2. Open the app on Android Studio and rebuild the gradle file.
3. (OPTIONAL STEP) Configure API keys: 
   - Open-Meteo API: No API_KEY required, Open-Meteo weather link already <weather_url1> in the code.
   - Google Maps API: API key already included in the code. In case the number of free requests expires get an API key [here](https://developers.google.com/maps/documentation/javascript/get-api-key) and replace `<mapsApi>` in the code.

## Testing Steps

1. Change the date, startTime, and endTime for any driver (eg: Ralph) under the assets/schedule.json file to match the current time and date.
2. Test the app against the credentials for the selected driver from the credentials.json file for sign-in.
3. click on the 'View Schedule' button to open the Work info page to view the driver's week schedule loaded onto a database from the schedule.json file.
   click the left chevron button on the top left to return to the dashboard
4. click the cloud-weather icon on the top left of the dashboard to view the current weather. click it again to make the weather info disappear.
5. switch the toggle on to view working locations. (if the current time and date match the scheduled time and date, the toggle switch is enabled)
6. click on any location radio button to view the student wait list for it. You can switch at any point.
7. click on a student's name to view their detailed info page.
8. The student info page contains their name, student ID, and drop-off location along with the 'Open in Google Maps' button.
9. click on the 'Open in Google Maps' button to view the student's drop-off location on Google Maps for easy navigation. return to the app.
    click the left chevron button on the top left to return to the dashboard
10. click on the remove button to remove a student from the waitlist. This will give a pop-up confirmation page asking if you are sure you want to remove the student.
    On pressing yes the student is removed from the waitlist, pressing cancel will just close the pop-up and have no changes.
11. you can now click on the 'log out' button to sign out of the account and return to the sign-in page.
12. Repeat steps 1-10 for another driver (eg: Akshay) whose shift timings don't match the current time and date (you can edit the schedule.json file for this if needed).
13. Upon signing in you can see that the toggle switch is no longer enabled. This is to prevent unnecessary toggles and malpractices from drivers not on schedule on the current date and time.
    

## Release Notes

### Version 1.0.0

- Added the ability for drivers to view their weekly schedule.
- Implemented a toggle for drivers to indicate their active shift status.
- Integrated Open-Meteo API to display weather information.
- Enhanced the user interface for better user experience.
- Added a weather icon button to display the current weather information.
- Added a callback feature to fix the async getAddressCoordinates() function for geocoding purposes.
- Added a Nested scroll view for the Driver Dashboard in case elements do not fit on the screen.
- Added a pop-up confirmation dialog when attempting to log out.

### Supported API Levels

- **Minimum API Level**: 26 (Android 8.0)
- API Level: 30 (Android 10)
- API Level: 33 (Android 13)
- **Target API Level**: 34 (Android 14)

## Known Issues

- The app currently uses mock values due to the unavailability of the UNB database for sign-in credentials.
- The app currently uses mock values due to the unavailability of the student interface for check-in waitList (put onto a database).
- The app currently uses mock values due to the unavailability of the driver schedule to update a database with all drivers' week schedules.
- Limited number of geocoding API request calls per day. (May cause the geocoding feature to fail)
- Limited testing on various devices and screen sizes.

## Contributors

- Tina Kashwani (Project Manager, CTO, Co-Developer)
- Pratik Mishra (Marketing Manager, UI/UX Designer, Co-Developer, Documentation Lead)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

