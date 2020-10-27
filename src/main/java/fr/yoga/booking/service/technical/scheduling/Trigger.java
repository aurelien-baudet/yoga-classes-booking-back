package fr.yoga.booking.service.technical.scheduling;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;

import org.apache.commons.beanutils.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class Trigger<T> {
	private final String id;
	private final T context;
	private final String type;
	private final Instant triggerAt;
	private final Runnable task;
	private final Instant cleanableAt;
	private final String contextId;
	
	public Trigger(String id, T context, String type, Instant triggerAt, Runnable task) {
		this(id, context, type, triggerAt, task, null);
	}

	public Trigger(String id, T context, String type, Instant triggerAt, Runnable task, Instant cleanableAt) {
		this(id, context, type, triggerAt, task, cleanableAt, getContextId(context));
	}

	private static <T> String getContextId(T context) {
		try {
			return BeanUtils.getProperty(context, "id");
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("Failed to get id for context "+context.getClass(), e);
		}
	}
}
