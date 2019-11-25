package fr.yoga.booking.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ExpressionParser {
	private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
	
	public static String evaluate(String expression, Object context) {
		Matcher m = EXPRESSION_PATTERN.matcher(expression);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, evaluateVariable(m.group(1), context));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private static String evaluateVariable(String expressionString, Object context) {
		SpelExpressionParser parser = new SpelExpressionParser();
		Expression expression = parser.parseExpression(expressionString);
		return expression.getValue(context, String.class);
	}

}
