require 'spec_helper'

describe "XML Transformation" do

  context "against schema 4" do
    let!(:template) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XSLT(File.read('/home/app/src/main/webapp/xsl/kernel4_to_oaidc.xsl')) }}

    describe "Good XML" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.14279.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23650.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML changed namespaces to kernel 3" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23656.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML no comments" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23651.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML no chinese" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23652.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML no empty tags" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23655.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML no language" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23659.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML Nothing but DOI" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23660.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          puts template
          puts template.serialize(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "full example from schema website" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.5072.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'it should not fail' do
          expect {template.transform(document)}.not_to raise_error
        end
    end
    describe "full example from schema website" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/D3P26Q35R.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'it should not fail' do
          expect {template.transform(document)}.not_to raise_error
        end
    end
    describe "different example" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.13145.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'it should not fail' do
          expect {template.transform(document)}.not_to raise_error
        end
    end
    describe "Bad XML without name spaces" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23661.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML without name spaces and no metadata but the DOI" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23662.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          puts document
          expect {template.transform(document)}.not_to raise_error
        end
    end


    describe "Bad XML remove all namespacing" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23663.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          puts document
          expect {template.transform(document)}.not_to raise_error
        end
    end
  end

  context "against schema 3" do
    let!(:template) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XSLT(File.read('/home/app/src/main/webapp/xsl/kernel3_to_oaidc.xsl')) }}

    describe "full example from schema website for schema 3" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/D3P26Q35R.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'it should not fail' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Good XML" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.14279.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23650.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML changed namespaces to kernel 3" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23656.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end
    describe "Bad XML changed to namespaces kernel 3 no titles" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23657.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML no titles no multiple names spaces" do
      let(:document) { Dir.chdir(RSPEC_ROOT) {Nokogiri::XML(File.read('fixtures/_10.23658.xml'), nil, 'UTF-8', &:noblanks) }}
        it 'generating formatted content' do
          expect {template.transform(document)}.not_to raise_error
        end
    end

  end
end
