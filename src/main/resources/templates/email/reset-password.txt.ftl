<#import "fragments/layout.txt.ftl" as layout>
<@layout.header />
Changement de mot de passe

Bonjour ${user.displayName},

Vous pouvez confirmer le changement de mot de passe en cliquant sur le lien ci-dessous
ou en saisissant le code suivant sur le site ou l'application : ${token}

Changer votre mot de passe : ${@deploymentContextService.resetPasswordUrl(token)}


Le lien et le code sont valables ${statics['fr.yoga.booking.util.DurationUtil'].format(@passwordResetProperties.tokenValidity)}.
