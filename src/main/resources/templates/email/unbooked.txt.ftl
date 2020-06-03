<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Désinscription confirmée

<@class.bookedClassPreview scheduledClass=bookedClass bookedFor=bookedFor />


---------------------------------------------

Merci de m'avoir prévenu

	<@utils.indent>
    Je vous remercie de m'avoir prévenu de votre abscence au cours du ${statics['fr.yoga.booking.util.DateRangeUtil'].format(bookedClass.start, bookedClass.end)}.
    
    J'espère vous revoir bientôt sur les tapis, Namaste!
    </@>
<@layout.footer />