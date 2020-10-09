<#import "fragments/layout.txt.ftl" as layout>
<@layout.header />
Renouvellement de ton abonnement mensuel

Bonjour ${subscription.subscriber.displayName},

Ton abonnement mensuel se termine le ${statics['fr.yoga.booking.util.DateUtil'].formatDate(subscription.monthCard.end)}.
Pense à le renouveller si tu souhaites continuer les cours de Yoga.
Tu peux également voir avec Cyril pour prendre un autre abonnement.

Merci
