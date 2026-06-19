package com.vote.common.util;

import org.springframework.web.util.HtmlUtils;

public class HtmlSanitizer {

  public static String sanitize(String input) {
    if (input == null) return null;
    return HtmlUtils.htmlEscape(input.trim());
  }
}
