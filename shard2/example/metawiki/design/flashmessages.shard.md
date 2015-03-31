Flash message could be implemented as a "static" list of messages (id + text) that are 
persistent across  requests but that auto-expire on the server after X seconds. In 
addition there is an endpoint where an Ajax request can request direct expiration 
of the message. We avoid any DoS this way and clean everything up.

This all assumes that our application is "single" user, i.e. we do not associate any
client-identity to the requests. Is this reasonable?

