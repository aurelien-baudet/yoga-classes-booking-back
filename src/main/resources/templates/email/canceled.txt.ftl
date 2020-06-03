<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Cours du ${statics['fr.yoga.booking.util.DateRangeUtil'].format(canceledClass.start, canceledClass.end)} annulé

<@class.classPreview scheduledClass=canceledClass />


---------------------------------------------

Message de ${canceledClass.lesson.teacher.displayName}

	<@utils.indent>
	${additionalInfo.message}
	</@>
<@layout.footer />
