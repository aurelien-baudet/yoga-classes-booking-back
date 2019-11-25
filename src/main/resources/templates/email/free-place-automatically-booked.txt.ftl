<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
Une place vient de se libérer et vous êtes maintenant inscrit au cours

<@class.bookedClassPreview scheduledClass=data.bookedClass bookedFor=data.student place=data.bookedClass.lesson.place>


	Voir les informations du cours : ${@deploymentContextService.viewClassUrl(data.bookedClass)}
	Vous désinscrire               : ${@deploymentContextService.unbookUrl(data.bookedClass)}
</@>


---------------------------------------------

Quelques informations supplémentaires

	<@information.approvedMessage />
	
	
<@information.description scheduledClass=data.bookedClass />
<@layout.footer />
