package de.cognicrypt.codegenerator.featuremodel.clafer;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.clafer.ast.AstConstraint;

public class ClaferModelUtils {

	/**
	 * Method to find a clafer with a given name in whole model
	 */
	public static AstClafer findClaferByName(final AstClafer inputClafer, final String name) {
		final String inputName = getNameWithoutScope(inputClafer.getName());
		if (inputName.equalsIgnoreCase(name)) {
			return inputClafer;
		} else {
			if (inputClafer.hasChildren()) {
				final AstConcreteClafer foundChildClafer = inputClafer.getChildren().stream().filter(child -> getNameWithoutScope(child.getName()).equals(name)).findFirst()
					.orElse(null);
				if (foundChildClafer != null) {
					return foundChildClafer;
				} else {
					for (final AstConcreteClafer childClafer : inputClafer.getChildren()) {
						final AstClafer foundClafer = findClaferByName(childClafer, name);
						if (foundClafer != null) {
							return foundClafer;
						}
					}
				}
				;

			}
			if (inputClafer instanceof AstAbstractClafer) {
				for (final AstAbstractClafer abstractChildClafer : ((AstAbstractClafer) inputClafer).getAbstractChildren()) {
					final AstClafer foundClafer = findClaferByName(abstractChildClafer, name);
					if (foundClafer != null) {
						return foundClafer;
					}
				}
			}

			if (inputClafer.hasRef()) {
				return findClaferByName(inputClafer.getRef().getTargetType(), name);
			}

			if (inputClafer.getSuperClafer() != null) {
				return findClaferByName(inputClafer.getSuperClafer(), name);
			}

			return null;

		}
	}

	/**
	 * Method takes AstClafer as an input and returns a description of the clafer if exist, returns name of the clafer otherwise
	 */
	// FIXME check if this method is used in any commented code
	public static String getDescription(final AstClafer inputClafer) {
		for (final AstConstraint child : inputClafer.getConstraints()) {
			final String expr = child.getExpr().toString();
			final int indexEqSign = expr.indexOf('=');
			if (expr.substring(0, indexEqSign > 0 ? indexEqSign : 1).contains("escription . ref")) {
				// return without Quotes,hence replaced the "" with empty
				return expr.substring(indexEqSign + 1, expr.length()).replace("\"", "");
			}
		}
		return inputClafer.getName();
	}

	public static AstConcreteClafer createClafer(final AstClafer taskClafer, final String name, final String type) {
		final AstConcreteClafer newClafer = taskClafer.addChild(name).withCard(1, 1);
		newClafer.refTo(ClaferModelUtils.findClaferByName(taskClafer.getParent(), type));
		return newClafer;
	}

	public static String getNameWithoutScope(final String input) {
		final int underScoreIndex = input.indexOf("_");
		if (underScoreIndex >= 0) {
			return input.substring(underScoreIndex + 1);
		} else {
			return input;
		}
	}

	/**
	 * method to check if the given clafer is an abstract clafer
	 *
	 * @param astClafer
	 * @return
	 */
	public static boolean isConcrete(final AstClafer astClafer) {
		Boolean isConcrete = true;
		if (astClafer.hasRef()) {
			isConcrete = !astClafer.getRef().getTargetType().getClass().toGenericString().contains("AstAbstractClafer");
		}
		if (astClafer.getClass().toGenericString().contains("AstAbstractClafer")) {
			isConcrete = false;
		}
		return isConcrete;
	}

	/**
	 * removes scope from name (e.g., c0_) and changes first letter of the string to Upper case example c0_scope will become Scope
	 */
	public static String removeScopePrefix(final String scope) {
		final String shortenedScope = scope.substring(scope.indexOf('_') + 1, scope.length());
		return shortenedScope.substring(0, 1).toUpperCase() + shortenedScope.substring(1, shortenedScope.length());
	}
}
