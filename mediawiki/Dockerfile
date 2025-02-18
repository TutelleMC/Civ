FROM mediawiki:latest

# Install Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Template Styles
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/TemplateStyles /var/www/html/extensions/TemplateStyles
RUN cd /var/www/html/extensions/TemplateStyles && composer install --no-dev

# JsonConfig
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/JsonConfig /var/www/html/extensions/JsonConfig

# MobileFrontend
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/MobileFrontend /var/www/html/extensions/MobileFrontend

# AdminLinks
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/AdminLinks /var/www/html/extensions/AdminLinks

# DynamicPageList3
RUN git clone --depth 1 https://github.com/Universal-Omega/DynamicPageList3.git /var/www/html/extensions/DynamicPageList3

# Contribution Scores
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/ContributionScores /var/www/html/extensions/ContributionScores

# Disambiguator
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/Disambiguator /var/www/html/extensions/Disambiguator

# Popups
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/Popups /var/www/html/extensions/Popups

# NoTitle
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/NoTitle /var/www/html/extensions/NoTitle

# OpenGraphMeta
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/OpenGraphMeta /var/www/html/extensions/OpenGraphMeta

# DisplayTitle
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/DisplayTitle /var/www/html/extensions/DisplayTitle

# Discord
RUN git clone --depth 1 https://github.com/jayktaylor/mw-discord.git /var/www/html/extensions/Discord

# Purge
RUN git clone --depth 1 https://github.com/AlPha5130/mediawiki-extensions-Purge.git /var/www/html/extensions/Purge

# TitleKey
RUN git clone --depth 1 https://gerrit.wikimedia.org/r/mediawiki/extensions/TitleKey /var/www/html/extensions/TitleKey

# Don't forget to run ``docker compose exec mediawiki php maintenance/run.php update`` when mediawiki is up after installation