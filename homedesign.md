IMPORTANT — READ THIS CAREFULLY BEFORE EDITING

I want to redesign ONLY the EXISTING HOME SCREEN.

DO NOT create a new Home Screen from the reference image.

The reference image is ONLY for:
- visual style
- color direction
- spacing feel
- card styling
- background treatment
- premium gaming appearance
- button styling
- overall hierarchy

The EXISTING Home Screen is the source of truth for:
- element sizes
- element positions
- text sizes
- existing component dimensions
- existing buttons
- existing cards
- existing div/container sizes
- existing navigation
- existing game content
- existing functionality

==================================================
1. PRESERVE EXISTING DIMENSIONS
==================================================

VERY IMPORTANT:

Do NOT blindly use the pixel dimensions from the reference screenshot.

My existing Home Screen already has:
- Name element
- Profile element
- Wallet/balance button
- Game buttons/cards
- Game containers
- existing text
- existing divs/boxes
- existing navigation buttons
- existing spacing/layout

KEEP THEIR CURRENT SIZE AND POSITION unless a small adjustment is absolutely necessary for the new visual style.

For example:

If my existing Name button/container is currently:
- 120px wide
- 40px high

DO NOT change it to the reference screenshot's dimensions.

Keep the existing dimensions.

Same rule applies to:
- buttons
- cards
- divs
- containers
- badges
- icons
- text areas
- navigation items
- game tiles

The goal is:

EXISTING LAYOUT/DIMENSIONS
+
REFERENCE VISUAL STYLE

NOT:

REFERENCE LAYOUT/DIMENSIONS
+
NEW COMPONENTS

==================================================
2. EXISTING NAME / PROFILE UI
==================================================

Keep my existing Name/Profile component exactly where it currently exists.

Preserve:
- its current width
- current height
- current position
- current text
- current functionality

Only improve its visual styling to fit the reference:

- premium dark-purple environment
- clean white typography
- appropriate purple/gold accents
- subtle borders
- subtle shadows
- rounded styling only if my existing component already supports it

DO NOT create another name box.

DO NOT duplicate the profile section.

==================================================
3. EXISTING WALLET / BALANCE BUTTON
==================================================

Keep my existing wallet/balance button.

Do NOT change its functional behavior.

Do NOT replace it with a new wallet component.

Keep its current dimensions and placement.

Only restyle it toward the reference:
- emerald/bright green background
- white balance text
- white wallet icon
- white + icon
- rounded premium appearance

If the existing button has different dimensions, KEEP THE EXISTING DIMENSIONS.

==================================================
4. REMOVE THE 3 CIRCULAR PROMOTIONAL ITEMS
==================================================

DO NOT ADD THESE.

The reference screenshot contains three circular promotional items.

They are NOT required.

Do NOT create:
- 30K promotion circle
- Winners Pro Trip circle
- Happy Winners circle
- circular avatar promotion strip
- flame badge attached to the circles
- any replacement promotional circle section

The existing Home Screen should flow directly into its existing game/content area.

==================================================
5. EXISTING GAME CARDS / BUTTONS
==================================================

Keep the existing game cards and game buttons.

Do NOT invent new games.

Do NOT replace existing game data.

Do NOT change game functionality.

Preserve the existing:
- width
- height
- position
- spacing
- artwork
- title
- prize
- PLAY action

Only restyle them to feel closer to the reference:

- dark/purple overall environment
- strong colorful game artwork
- rounded card appearance
- clean white PLAY button
- subtle depth/shadow
- premium gaming UI

If a game card already has a specific size, KEEP THAT SIZE.

==================================================
6. ALL EXISTING DIVS / BOXES
==================================================

This is critical.

Inspect the existing Home Screen DOM/component hierarchy before changing anything.

For every existing:
- div
- container
- box
- card
- button
- text container
- image container

preserve its current dimensions unless there is a clear visual reason to adjust it.

Do NOT rebuild the layout unnecessarily.

Prefer modifying:
- background
- border
- border-radius
- shadow
- typography styling
- gradients
- icon styling
- spacing only where necessary

instead of replacing components.

==================================================
7. COLORS
==================================================

Move the existing Home Screen toward the reference color language.

Main background:
deep premium purple.

Approximate direction:
#26002F
#280031
#3B0A57

Primary green:
#08C98B

White:
#FFFFFF

Secondary text:
light lavender

Gold accents:
#E5C13A

Do NOT make every component purple.

Maintain strong contrast between:
- background
- cards
- buttons
- text
- game artwork

==================================================
8. TYPOGRAPHY
==================================================

Keep the existing text content.

Do NOT rename existing buttons or games.

Use the project's existing font if it already has a suitable font.

Otherwise use a Poppins-like modern gaming UI font.

Typography should feel:
- bold
- clean
- compact
- mobile-friendly

Do NOT arbitrarily increase/decrease existing text sizes.

Preserve existing text dimensions as much as possible.

==================================================
9. BOTTOM NAVIGATION — ONLY 4 BUTTONS
==================================================

VERY IMPORTANT:

The Home Screen must have ONLY 4 bottom navigation buttons.

NOT 5.

Remove/avoid the fifth navigation item from the reference concept.

Use the EXISTING 4 navigation items/routes from my project.

Do NOT invent a new fifth item.

Do NOT copy the reference's five-item navigation.

Layout:

4 equal navigation items across the full screen width.

For a 411px wide screen:

approximately:
411 / 4 = 102.75px per item

But this is only a layout principle.

Use responsive width:
screenWidth / 4

The navigation should remain anchored to the bottom.

==================================================
10. BOTTOM NAV VISUAL STYLE
==================================================

Keep the EXISTING 4 navigation items and their functionality.

Only improve visual styling:

- deep purple navigation background
- subtle upper highlight/border
- clean icons
- small labels
- active Home state with a slightly brighter purple rounded background
- inactive items in light lavender
- active item in white

Do NOT change navigation routes.

Do NOT change navigation behavior.

Do NOT add a fifth item.

==================================================
11. RESPONSIVE LAYOUT
==================================================

Do NOT hardcode the entire screen to 411x856.

The reference image is only a visual reference.

The current project's responsive behavior must remain intact.

Preserve the existing component sizing rules.

Make sure the redesign works on:
- small Android phones
- normal Android phones
- larger Android phones

==================================================
12. EXISTING FUNCTIONALITY MUST REMAIN
==================================================

DO NOT break:

- profile
- wallet
- balance
- game launch
- PLAY buttons
- game selection
- API data
- authentication
- navigation
- bottom navigation
- existing animations
- existing state management

This is a UI redesign, NOT a logic rewrite.

==================================================
13. BEFORE CODING
==================================================

First inspect the existing Home Screen.

Identify:
1. Home Screen main file
2. Header component
3. Name/Profile component
4. Wallet component
5. Existing game card component
6. Existing game buttons
7. Existing div/container structure
8. Bottom navigation component
9. Existing 4 navigation items
10. Existing CSS/theme/design tokens
11. Existing assets

Then create a SHORT implementation plan.

The plan must explicitly state:

- which files will be modified
- which existing components will be reused
- which components will only be restyled
- which promotional elements are intentionally NOT being added
- how the existing 4-item navigation will be preserved

DO NOT modify unrelated files.

==================================================
14. IMPLEMENTATION PRINCIPLE
==================================================

Use this exact principle:

EXISTING COMPONENT
        +
EXISTING SIZE
        +
EXISTING POSITION
        +
EXISTING FUNCTIONALITY
        +
REFERENCE VISUAL STYLE
        =
FINAL HOME SCREEN

Do NOT use:

REFERENCE COMPONENT
        +
REFERENCE SIZE
        +
NEW LAYOUT
        =
FINAL HOME SCREEN

==================================================
15. FINAL CHECK
==================================================

After implementation verify:

✓ Existing Home Screen still works
✓ Existing name/profile remains
✓ Existing wallet remains
✓ Existing game cards remain
✓ Existing game data remains
✓ Existing PLAY buttons remain functional
✓ Existing sizes are preserved
✓ Existing div/container structure is preserved where possible
✓ No 3 circular promotional banners
✓ No circular promotional replacement
✓ ONLY 4 bottom navigation buttons
✓ Existing 4 navigation routes still work
✓ No fifth navigation button
✓ No unrelated files changed
✓ No backend/business logic changed
✓ No fake data added
✓ No layout overflow
✓ No broken responsive behavior

The result should look like my existing Home Screen has been professionally redesigned using the visual language of the reference screenshot — NOT like a completely new screen copied from the reference.