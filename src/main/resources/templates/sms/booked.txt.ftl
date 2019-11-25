Bonjour ${data.bookedFor.displayName}, je vous remercie de votre inscription au cours du ${statics['fr.yoga.booking.util.DateRangeUtil'].format(data.bookedClass.start, data.bookedClass.end)}.
<#if data.isApproved()>Prévoyez de venir 15min en avance pour vous installer calmement.</#if>
<#if !data.isApproved()>Le cours étant complet, vous êtes inscrit sur la file d'attente. Dès qu'une place se libère, vous recevez un SMS pour vous indiquer que vous êtes automatiquement inscrit.</#if>
A bientôt sur le tapis, Namaste!