<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Cours du ${statics['fr.yoga.booking.util.DateRangeUtil'].format(data.canceledClass.start, data.canceledClass.end)} annulé

<@class.classPreview scheduledClass=data.canceledClass />


---------------------------------------------

Message de ${data.canceledClass.lesson.teacher.displayName}

	<@utils.indent>
	${data.additionalInfo.message}
	</@>
<@layout.footer />
