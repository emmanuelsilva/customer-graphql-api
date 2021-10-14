package dev.emmanuel.banking.helper;

import org.mockito.stubbing.Answer;

import java.util.function.Function;

public interface MockitoAnswerPipeline<T> extends Answer<T> {

  static <R> MockitoAnswerPipeline<R> will(Answer<R> answer) {
    return answer::answer;
  }

  default <R> MockitoAnswerPipeline<R> as(Class<R> type) {
    return to(type::cast);
  }

  default <R> MockitoAnswerPipeline<R> to(Function<T, R> mapper) {
    return it -> mapper.apply(answer(it));
  }

}
