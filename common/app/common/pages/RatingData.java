package common.pages;

import common.cms.CmsPage;

import java.util.List;

import static java.util.Arrays.asList;

public class RatingData {
    private final CmsPage cms;

    public RatingData(final CmsPage cms) {
        this.cms = cms;
    }

    public List<SelectableData> getRating() {
        return asList(
                new SelectableData("5 Stars", "5", cms.getOrEmpty("ratingFiveStarText"), "", false),
                new SelectableData("4 Stars", "4", cms.getOrEmpty("ratingFourStarText"), "", false),
                new SelectableData("3 Stars", "3", cms.getOrEmpty("ratingThreeStarText"), "", false),
                new SelectableData("2 Stars", "2", cms.getOrEmpty("ratingTwoStarText"), "", false),
                new SelectableData("1 Stars", "1", cms.getOrEmpty("ratingOneStarText"), "", false));
    }
}
