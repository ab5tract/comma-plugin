get -> LoggedIn $user, 'releases' {
    if $db.has-active-subscription(user-id => $user.user-id) {
        template 'releases.crotmp', %( releases => $downloads.releases );
    }
    <caret>redirect '/subscriber/home';
    else {
    }
}
