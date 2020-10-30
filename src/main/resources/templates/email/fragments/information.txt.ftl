<#import "utils.txt.ftl" as utils>
<#macro approvedMessage>
	<@utils.indent>
    Vous avez réservé un cours de yoga avec moi et je vous en remercie.
	Prévoyez de venir 15min en avance pour vous garer calmement, éviter de vous garer devant le portail des voisins, que le portail soit un portail pour voiture ou non, cela arrive fréquemment et c’est un aspect très sensible dans cette rue, donc dans la mesure du possible et pour faciliter les bonnes relations avec le voisinage, éloignez vous de la rue Saint Expédit pour garer votre voiture. 
	 
	
	Je limite les places afin d'être au maximum avec vous, pour vous accompagner et vous emmener toujours un peu plus loin.
	Essayez de venir le ventre léger pour ne pas bloquer la respiration.
	
	Si vous ne vous sentez pas en grande forme avant le cours, venez 😉
	C'est là que le yoga prend tout son sens.
	Vous vous sentirez probablement mieux que si vous n'étiez pas venu.
	
	Si jamais vous avez un empêchement, pensez à annuler la réservation afin que quelqu'un puisse prendre votre place.
	Plus vous prévenez tôt, mieux c'est pour les autres 🙂
	Vous pouvez annuler jusqu'à ${statics['fr.yoga.booking.util.DurationUtil'].format(@bookingProperties.unbookUntil)} avant le début du cours. Au delà, il n'est plus possible d'annuler et votre séance est décomptée. Si vous avez un imprévu, vous pouvez tout de même me contacter pour qu'on s'arrange.
	
	Les tarifs sont disponibles sur https://www.yogasaintpierre.fr/tarifs
	
	A bientôt sur le tapis, Namaste!
    </@>
</#macro>

<#macro waitingMessage>
	<@utils.indent>
    Vous avez réservé un cours de yoga avec moi et je vous en remercie.
    Le cours étant complet, vous êtes inscrit sur la file d'attente.
    
    Dès qu'une place se libère, vous recevez un mail pour vous vous demander de confirmer votre présence.
    
    A bientôt sur le tapis, Namaste!
    </@>
</#macro>


<#macro description scheduledClass>
---------------------------------------------

Informations concernant le cours réservé

    <@utils.indent>
    ${scheduledClass.lesson.info.description}
    </@>
</#macro>
