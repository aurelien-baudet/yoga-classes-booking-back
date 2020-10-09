<#import "fragments/layout.txt.ftl" as layout>
<@layout.header />
Renouvellement de ton abonnement annuel

Bonjour ${subscription.subscriber.displayName},

Ton abonnement annuel se termine le ${statics['fr.yoga.booking.util.DateUtil'].formatDate(subscription.annualCard.end)}.
Pense à le renouveller si tu souhaites continuer les cours de Yoga.
Tu peux également voir avec Cyril pour prendre un autre abonnement.

Merci
