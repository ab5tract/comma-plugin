class WebService::EveOnline::ESI::Character {
    method putCalendarEvent($eventid, $response, :$datasource) {
        die "<eventID> must be an integer" unless $eventid ~~ Int;
        die "<response> must be one of 'accepted', 'declined' or 'tentative'"
                unless $response eq <accepted declined tentative>.all();

        self.checkScope('esi-calendar.respond_calendar_events.v1');
        self.putBodyByPrefix(
                "{ self.sso.characterID }/calendar/{ $eventid }/",
                :body({ response => $response });
                :$datasource,
                );
    }
}