/*
 *
 * DIY Layout Creator (DIYLC). Copyright (c) 2009-2025 held jointly by the individual authors.
 *
 * This file is part of DIYLC.
 *
 * DIYLC is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * DIYLC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with DIYLC. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 */
package org.diylc.netlist;

import java.util.List;
import java.util.Map;

public class NetlistSummaryHtmlReport {

    public static String generateHtml(List<Summary> summaries, String fontName) {
        return generateHtml(summaries, fontName, Map.of());
    }

    public static String generateHtml(List<Summary> summaries, String fontName,
                                      Map<String, List<Integer>> switchPositionCounts) {
        if (summaries == null || summaries.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder("<html><head><style>");
        sb.append("body { font-family: ").append(fontName)
            .append(", sans-serif; color: black; margin: 10px; }");
        sb.append("h2 { font-size: 12px; margin-top: 10px; margin-bottom: 0px; }");
        sb.append("p { margin: 0; }");
        sb.append("hr { margin-top: 10px; margin-bottom: 0; }");
        sb.append("</style></head><body>");

        StringBuilder ganged = new StringBuilder();
        StringBuilder incompatible = new StringBuilder();
        for (var entry : switchPositionCounts.entrySet()) {
            List<Integer> counts = entry.getValue();
            if (counts.size() < 2)
                continue;

            String name = entry.getKey().replace("&", "&amp;")
                    .replace("<", "&lt;").replace(">", "&gt;");
            if (counts.stream().distinct().count() == 1) {
                ganged.append("<li>").append(name).append(": ")
                        .append(counts.size()).append(" switches, ")
                        .append(counts.get(0)).append(" positions</li>");
            } else {
                incompatible.append("<li>").append(name)
                        .append(": position counts ").append(counts)
                        .append("</li>");
            }
        }

        if (incompatible.length() > 0) {
            sb.append("<p><b>Warning: same-name switches have different numbers ")
                    .append("of positions.</b> The switches listed below were not ")
                    .append("ganged and were analyzed independently.</p><ul>")
                    .append(incompatible).append("</ul><hr>");
        }

        if (ganged.length() > 0) {
            sb.append("<p><b>Same-name switches are assumed to be parts of one switch.</b> ")
                    .append("The groups listed below have matching position counts. ")
                    .append("Their positions are synchronized by position number, ")
                    .append("even if their position labels differ. Each group is ")
                    .append("represented as a single switch in configuration descriptions.</p><ul>")
                    .append(ganged).append("</ul><hr>");
        }

        for (Summary summary : summaries) {
            if (summaries.size() > 1) {
                sb.append("<h2>Switch configuration: ").append(summary.getNetlist().getSwitchSetupString())
                    .append("</h2>");
            }

            sb.append(summary.getSummaryHtml());

            if (summaries.size() > 1) {
                sb.append("<br><hr>");
            }
        }
        sb.append("</body></html>");

        return sb.toString();
    }
}
