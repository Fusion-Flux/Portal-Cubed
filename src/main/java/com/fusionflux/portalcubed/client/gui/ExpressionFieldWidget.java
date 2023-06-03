package com.fusionflux.portalcubed.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExpressionFieldWidget extends EditBox {
    @Nullable
    private Expression expression;
    @Nullable
    private String error;
    private Function<String, Expression> parser = s -> new ExpressionBuilder(s).build();
    private Consumer<String> changedListener2 = s -> {};

    public ExpressionFieldWidget(Font textRenderer, int x, int y, int width, int height, Component text) {
        super(textRenderer, x, y, width, height, text);
        super.setResponder(s -> {
            changedListener2.accept(s);
            onValueChange(s);
        });
        super.setFormatter(this::getTextToRender);
        setMaxLength(256);
    }

    public void setExpression(String expression) {
        setValue(expression);
        setExpression0(expression);
    }

    @Nullable
    public Expression getExpression() {
        return expression;
    }

    public void setParser(Function<String, Expression> parser) {
        this.parser = parser;
    }

    @Override
    public void setResponder(Consumer<String> changedListener) {
        changedListener2 = changedListener;
    }

    private void onValueChange(String text) {
        setExpression0(text);
    }

    @Override
    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> renderTextProvider) {
        throw new UnsupportedOperationException("Cannot set renderTextProvider for ExpressionFieldWidget.");
    }

    private FormattedCharSequence getTextToRender(String text, int cursor) {
        Style style = Style.EMPTY;
        if (error != null) {
            style = style.applyFormat(ChatFormatting.RED);
        }
        return FormattedCharSequence.forward(text, style);
    }

    private void setExpression0(String expression) {
        if (expression.isEmpty()) {
            this.expression = null;
            setError(null);
            return;
        }
        try {
            this.expression = parser.apply(expression);
            final ValidationResult validation = this.expression.validate(false);
            if (validation.isValid()) {
                setError(null);
            } else {
                setError(validation.getErrors().get(0));
            }
        } catch (RuntimeException e) {
            this.expression = null;
            setError(cleanError(e));
        }
    }

    public void setError(String error) {
        this.error = error;
        if (error != null) {
            expression = null;
            setSuggestion("  (" + error + ")");
        } else {
            setSuggestion(null);
        }
    }

    public static String cleanError(Throwable t) {
        return StringUtils.defaultIfEmpty(t.getLocalizedMessage(), t.getClass().getSimpleName());
    }
}
