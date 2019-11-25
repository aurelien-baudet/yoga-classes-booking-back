<#import "utils.txt.ftl" as utils>
<#macro approvedMessage>
	<@utils.indent>
    Vous avez réservé un cours de yoga avec moi et je vous en remercie.
    Prévoyez de venir 15min en avance pour vous installer calmement.

    Je limite les places afin d'être au maximum avec vous, pour vous accompagner et vous emmener toujours un peu plus loin.
    Essayez de venir le ventre léger pour ne pas bloquer la respiration.

    Si vous ne vous sentez pas en grande forme avant le cours, venez ;)
    C'est là que le yoga prend tout son sens.
    Vous vous sentirez probablement mieux que si vous n'étiez pas venu.

    Si jamais vous avez un empêchement, pensez à annuler la reservation afin que quelqu'un puisse prendre votre place.
    Plus vous prévenez tôt, mieux c'est pour les autres :)

    La participation est libre, cependant on peut convenir d'un forfait mensuel ensemble afin que vous puissiez venir en illimité sur un mois.

    A bientôt sur le tapis, Namaste!
    </@>
</#macro>

<#macro waitingMessage>
	<@utils.indent>
    Vous avez réservé un cours de yoga avec moi et je vous en remercie.
    Le cours étant complet, vous êtes inscrit sur la file d'attente.
    
    Dès qu'une place se libère, vous recevez un mail pour vous indiquer que vous êtes automatiquement inscrit.
    
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
