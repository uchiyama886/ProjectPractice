ANT	= env LC_ALL=ja_JP.UTF-8 ant
ARCHIVE	= $(shell basename `pwd`)
SOURCES	= $(shell find . -name "*.java")
STYLE_YAML	= clang-format-for-java.yaml
STYLE_CONF	= _clang-format

all:
	$(ANT) all

clean:
	$(ANT) clean
	@rm -rf ResultImages

test:
	$(ANT) test

install:
	$(ANT) install

doc:
	$(ANT) doc

wipe: clean
	@find . -name ".DS_Store" -exec rm {} ";" -exec echo rm -f {} ";"
	(cd ../ ; rm -f ./$(ARCHIVE).zip)

zip:
	$(ANT) zip

format:
	@rm -f $(STYLE_CONF) ; ln -s $(STYLE_YAML) $(STYLE_CONF)
	for each in $(SOURCES) ; do echo ---[$${each}]--- ; clang-format -style=file $${each} ; echo ; done
	@rm -f $(STYLE_CONF)

app: install
	@xattr -cr ./Wavelet.app
	open ./Wavelet.app